package weilai.team.officialWebSiteApi.service.recruit.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.minio.*;
import io.minio.messages.ListMultipartUploadsResult;
import io.minio.messages.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.config.MinioConfigProperties;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.recruit.DO.CustomMinioClient;
import weilai.team.officialWebSiteApi.entity.recruit.DO.FileUploadInfo;
import weilai.team.officialWebSiteApi.entity.recruit.DO.Files;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.MergerFileDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.UserInfoExcel;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileDirEnum;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileTypeEnum;
import weilai.team.officialWebSiteApi.listener.ExcelDataListener;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.FilesMapper;
import weilai.team.officialWebSiteApi.service.recruit.FileService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author lzw
 * @date 2024/11/11 16:18
 * @description 作用：文件上传的业务
 */
@Service
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    /*
     * minio配置类
     */
    @Resource
    private MinioConfigProperties minioConfigProperties;

    @Resource
    private UserMapper userMapper;


    @Resource
    private RedisUtil redisUtil;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private FilesMapper filesMapper;


    private Integer expireTime = 1;

    /*
     * 自定义minio客户端
     */
    @Resource
    private CustomMinioClient minioClient;


    //根据md5获取信息
    @Override
    public ResponseResult<?> getFileByMD5(String fileMd5) {
        // 查询数据库是否上传成功
        Files one = filesMapper.selectOne(new LambdaQueryWrapper<Files>().eq(Files::getFileMd5, fileMd5));
        if (one != null) {
            LogUtil.info("first  -----------------------> 查询mysql,文件已经上传过");
            FileUploadInfo mysqlsFileUploadInfo = new FileUploadInfo();
            BeanUtils.copyProperties(one, mysqlsFileUploadInfo);
            return ResponseResult.FILE_FOUND.put(mysqlsFileUploadInfo);
        }
        //判断是否创建bucket
        String bucketName = minioConfigProperties.getBucketName();
        if (!minioUtil.createBucketIfNotExists(bucketName)) {
            //创建bucket失败，返回错误
            return ResponseResult.SERVICE_ERROR;
        }
        // 从redis获取文件数据
        FileUploadInfo fileUploadInfo = (FileUploadInfo) redisUtil.get(fileMd5);
        //redis中不为空
        if (fileUploadInfo != null) {
            LogUtil.info("first{断点续传}-----------------------> 查询redis，文件正在上传，返回分片信息");
            // 查询返回已经上传的分片索引
            List<Integer> chunkList = minioUtil.getChunkByFileMD5(minioUtil.getObjectName(fileMd5, fileUploadInfo.getFileName()), fileUploadInfo.getUploadId(), minioConfigProperties.getBucketName());
            if (chunkList != null) {
                fileUploadInfo.setChunkUploadedList(chunkList);
            }
            return ResponseResult.FILE_UPLOADING.put(fileUploadInfo);
        }
        return ResponseResult.FILE_NOT_FOUND;
    }


    /**
     * 初始化文件分片上传
     *
     * @param fileUploadInfo
     * @return Mono<Map < String, Object>>
     */
    @Override
    public ResponseResult<?> initMultiPartUpload(FileUploadInfo fileUploadInfo) {
        FileUploadInfo redisFileUploadInfo = (FileUploadInfo) redisUtil.get(fileUploadInfo.getFileMd5());
        if (redisFileUploadInfo != null) {
            LogUtil.info("从redis中获取到分片上传信息：" + redisFileUploadInfo);
            fileUploadInfo = redisFileUploadInfo;
        }
        log.info("开始初始化分片上传任务:{}",fileUploadInfo);
        // 分片上传
        Integer chunkNum = fileUploadInfo.getChunkNum();
        log.info("分片数量:{}",chunkNum);
        //就一个分片，直接上传整个文件
        if (chunkNum.equals(1)) {
            Files files = saveFileToDB(fileUploadInfo);
            String objectName = files.getUrl().substring(files.getUrl().lastIndexOf("/") + 1);
            Map<String, Object> uploadObjectUrl = minioUtil.getUploadObjectUrl(objectName, minioConfigProperties.getBucketName());
            return ResponseResult.FILE_IS_SINGLE.put(uploadObjectUrl);
        }
        Map<String, Object> map = minioUtil.initMultiPartUpload(fileUploadInfo);
        if (map == null) {
            return ResponseResult.FILE_INIT_FAIL;
        }
        String uploadId = (String) map.get("uploadId");
        fileUploadInfo.setUploadId(uploadId);
        // 将文件信息存入redis中, 设置一天过期
        log.info("设置redis中分片上传信息：" + fileUploadInfo);
        redisUtil.set(fileUploadInfo.getFileMd5(), fileUploadInfo, expireTime * 60 * 60 * 24);
        return ResponseResult.FILE_INIT_SUCCESS.put(map);
    }

    /**
     * 文件合并
     *
     * @param
     * @return String
     */
    @Override
    public ResponseResult<?> mergeMultipartUpload(MergerFileDTO mergerFileDTO) {


        log.info("tip message: 通过 <{}> 开始合并<分片上传>任务",mergerFileDTO);
        //redis中获取文件的fileName
        FileUploadInfo redisFileUploadInfo = (FileUploadInfo) redisUtil.get(mergerFileDTO.getFileMd5());
        if (redisFileUploadInfo == null) {
            LogUtil.info("message--------------redis中获取不到文件信息，返回失败");
            return ResponseResult.FILE_MERGER_ERROR;
        }

        // 添加分片验证逻辑
        String objectName = minioUtil.getObjectName(mergerFileDTO.getFileMd5(), mergerFileDTO.getFileName());
        List<Integer> chunkList = minioUtil.getChunkByFileMD5(objectName, mergerFileDTO.getUploadId(), minioConfigProperties.getBucketName());
        if (chunkList == null || chunkList.isEmpty()) {
            LogUtil.info("message--------------没有找到有效的分片信息，无法合并");
            redisUtil.del(redisFileUploadInfo.getFileMd5());
            return ResponseResult.FILE_MERGER_ERROR;
        }

        //根据文件的fileName和uploadId和bucketName上传
        boolean result = minioUtil.mergeMultipartUpload(objectName, mergerFileDTO.getUploadId(), minioConfigProperties.getBucketName());
        log.info("合并结果：" + result);
        //合并成功
        if (result) {
            //存入数据库
            Files files = saveFileToDB(redisFileUploadInfo);
            redisUtil.del(redisFileUploadInfo.getFileMd5());
            return ResponseResult.FILE_MERGER_SUCCESS.put(files);
        }
        return ResponseResult.FILE_MERGER_ERROR;
    }



    // 保存文件到数据库,为文件设置url和文件名
    private Files saveFileToDB(FileUploadInfo fileUploadInfo) {
        String suffix = fileUploadInfo.getFileName().substring(fileUploadInfo.getFileName().lastIndexOf("."));
        String url = this.getFilePath(minioConfigProperties.getBucketName(), fileUploadInfo.getFileMd5() + suffix);
        //存入数据库
        Files files = new Files();
        BeanUtils.copyProperties(fileUploadInfo, files);
        files.setBucketName(minioConfigProperties.getBucketName());
        files.setUrl(url);
        filesMapper.insert(files);
        return files;
    }

    //根据文件的bucket和fileName获取完整的文件路径
    public String getFilePath(String bucketName, String fileName) {
        return minioUtil.getFilePath(bucketName, fileName);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------


    /**
     * 上传文件的私有方法
     * 调用要判断是不是需要的类型，不是的话返回FILE_TYPE_ERROR
     *
     * @param fileDir 文件存放的目录
     * @param file    文件
     * @param filePrefixName 文件前缀名
     * @return 文件的url, 返回null的话代表出现异常
     */
    @Override
    public String uploadFile(MultipartFile file, FileDirEnum fileDir,String filePrefixName,String type) {
        try {
            //先判断bucket是否存在，不存在就创建一个
            if (!minioUtil.createBucketIfNotExists(minioConfigProperties.getBucketName())) {
                LogUtil.Error("bucket不存在，创建bucket失败",null);
                return null;
            }
            //命名为wlgzs-official-website/fileDir/theCurrentTime/studentId + 文件名
            String theCurrentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            //加一个/表示创建一个文件夹
            String filename = fileDir.getDirName() + "/" + theCurrentTime + "/" + filePrefixName + "." + type;
            LogUtil.info("生成文件的名称为" + filename);
            minioClient.putObject(PutObjectArgs.builder().
                    bucket(minioConfigProperties.getBucketName())
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    //文件上传的类型，如果不指定，那么每次访问时都要先下载文件
                    .contentType(file.getContentType())
                    .build());
            return minioConfigProperties.getEndpoint() + "/" + minioConfigProperties.getBucketName() + "/" + filename;
        } catch (Exception e) {
            LogUtil.Error("文件上传失败", e);
            return null;
        }
    }

//    @Transactional
//    @Override
//    public ResponseResult<?> uploadExcelFile(MultipartFile file) {
//        if (file == null || !FileUtil.isExpectedFileType(file, FileTypeEnum.EXCEL)) {
//            return ResponseResult.FILE_TYPE_ERROR;
//        }
//        try {
//            EasyExcel.read(file.getInputStream(), UserInfoExcel.class, new PageReadListener<UserInfoExcel>(dataList -> {
//                for (UserInfoExcel userInfo : dataList) {
//                    LogUtil.info("读取到一条数据" + userInfo);
//                    User user = new User();
//                    BeanUtils.copyProperties(userInfo, user);
//                    user.setUsername(userInfo.getStudyId());
//                    user.setPassword(Values.DEFAULT_PASSWORD);
//                    userMapper.insert(user);
//                }
//            })).sheet().doRead();
//        } catch (Exception e) {
//            LogUtil.Error("文件读取失败|数据插入失败", e);
//            //返回服务器内部异常
//            return ResponseResult.SERVICE_ERROR;
//        }
//        return ResponseResult.OK;
//    }

    @Transactional
    @Override
    public ResponseResult<?> uploadExcelFile(MultipartFile file) {
        if (file == null || !FileUtil.isExpectedFileType(file, FileTypeEnum.EXCEL)) {
            return ResponseResult.FILE_TYPE_ERROR;
        }
        //创建监听器
        ExcelDataListener listener = new ExcelDataListener();
        try {
            EasyExcel.read(file.getInputStream(), UserInfoExcel.class, listener).sheet().headRowNumber(1).doRead();
            boolean flag = listener.flag;
            final List<UserInfoExcel> data = listener.getData();
            if (!flag || data.isEmpty()){
                return ResponseResult.FILE_DATA_ERROR;
            }
            for (UserInfoExcel userInfo : data) {
                    LogUtil.info("读取到一条数据" + userInfo);
                    User user = new User();
                    BeanUtils.copyProperties(userInfo, user);
                    user.setUsername(userInfo.getStudyId());
                    user.setPassword(Values.DEFAULT_PASSWORD);
                    userMapper.insert(user);
                }
        }catch (IOException e) {
            LogUtil.Error("文件读取失败", e);
            //设置立即回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //返回服务器内部异常
            return ResponseResult.FILE_LOAD_ERROR;
        }catch (DuplicateKeyException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LogUtil.Error("数据重复", e);
            return ResponseResult.ERROR_INSERT;
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LogUtil.Error("服务器内部异常", e);
            return ResponseResult.SERVICE_ERROR;
        }
        return ResponseResult.OK;
    }
}

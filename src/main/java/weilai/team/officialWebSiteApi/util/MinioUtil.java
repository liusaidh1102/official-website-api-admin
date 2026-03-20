package weilai.team.officialWebSiteApi.util;

import com.google.common.collect.HashMultimap;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.config.MinioConfigProperties;
import weilai.team.officialWebSiteApi.entity.recruit.DO.CustomMinioClient;
import weilai.team.officialWebSiteApi.entity.recruit.DO.FileUploadInfo;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static weilai.team.officialWebSiteApi.entity.recruit.enums.FileDirEnum.RECRUIT;

/**
 * @author lzw
 * @date 2025/2/17 8:27
 * @description 作用：文件分上传工具类
 */
@Slf4j
@Component
public class MinioUtil {

    @Resource
    private CustomMinioClient customMinioClient;

    private static final Integer expiry = 1;

    @Resource
    private MinioConfigProperties minioConfigProperties;

    /**
     * 单文件签名上传
     *
     * @param objectName 文件全路径名称
     * @param bucketName 桶名称
     * @return /
     */
    public Map<String, Object> getUploadObjectUrl(String objectName, String bucketName) {
        try {
            log.info("tip message: 通过 <{}-{}> 开始单文件上传<minio>", objectName, bucketName);
            Map<String, Object> resMap = new HashMap();
            List<String> partList = new ArrayList<>();
            String url = customMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiry, TimeUnit.DAYS)
                            .build());
            log.info("tip message: 单个文件上传成功");
            partList.add(url);
            resMap.put("uploadId", "SingleFileUpload");
            resMap.put("urlList", partList);
            return resMap;
        } catch (Exception e) {
            log.error("error message: 单个文件上传失败、原因:", e);
            // 返回 文件上传失败
            return null;
        }
    }

    public void setBucketPolicyToPublic(String bucketName) throws Exception {
        // 设置存储桶策略为公开
        String policyJson = "{"
                + "\"Version\":\"2012-10-17\","
                + "\"Statement\":["
                + "    {"
                + "        \"Effect\":\"Allow\","
                + "        \"Principal\":\"*\","
                + "        \"Action\":\"s3:GetObject\","
                + "        \"Resource\":\"arn:aws:s3:::" + bucketName + "/*\""
                + "    }"
                + "]"
                + "}";

        customMinioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policyJson)
                        .build()
        );
    }


    public String uploadImage(MultipartFile file, String bucketName) {
        //生成文件名，随机生成一个15位字符串
        String objectName = RandomStringUtils.randomAlphanumeric(15) + ".png";
        return uploadFile(file, bucketName, objectName);
    }

    //判断bucket是不是存在，不存在创建
    public boolean createBucketIfNotExists(String bucketName) {
        try {
            boolean bucketExists = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                //设置bucket为公开，默认是private
                setBucketPolicyToPublic(bucketName);
            }
            return true;
        } catch (Exception e) {
            log.error("error message: 创建bucket失败、原因:", e);
            return false;
        }
    }


    /**
     * 文件上传
     *
     * @param file 文件,用户指定存储桶
     * @return 文件地址, null为失败
     */
    public String uploadFile(MultipartFile file, String bucketName, String objectName) {
        if (file == null || StringUtils.isBlank(objectName)) {
            return null;
        }
        //先判断bucket是否存在，不存在就创建一个
        if (!createBucketIfNotExists(bucketName)) {
            return null;
        }
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            customMinioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("error message: 文件上传失败、原因:", e);
            return null;
        }
        //返回文件路径
        String filePath = getFilePath(bucketName, objectName);
        log.info("文件名为：" + filePath);
        return filePath;
    }

    /**
     * 文件上传,业务类型+文件名
     *
     * @param serviceName
     * @param object
     * @return
     */
    public String uploadFile(String serviceName, String object, MultipartFile file) {
        String theCurrentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        object = serviceName + "/" + theCurrentTime + "/" + object;
        try {
            customMinioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfigProperties.getBucketName())
                            .object(object)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
            return getFilePath(minioConfigProperties.getBucketName(), object);
        } catch (Exception e) {
            LogUtil.Error("文件上传失败", e);
            return null;
        }
    }


    //根据bucket和objectName删除文件
    public Boolean deleteFile(String bucketName, String objectName) {
        try {
            customMinioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (Exception e) {
            log.error("error message: 文件删除失败、原因:", e);
            return false;
        }
    }

    //根据url删除文件
    public Boolean deleteFile(String url) {
        String objectName = getObjectName(url);
        return deleteFile(minioConfigProperties.getBucketName(), objectName);
    }

    //从url中获取文件的objectName
    public String getObjectName(String url) {
        int lastIndex = url.lastIndexOf("/");
        return url.substring(lastIndex + 1);
    }


    /**
     * 通过 sha256 获取上传中的分片信息
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return Mono<Map < String, Object>>
     */
    public List<Integer> getChunkByFileMD5(String objectName, String uploadId, String bucketName) {
        log.info("通过 <{}-{}-{}> 查询<minio>上传分片数据", objectName, uploadId, bucketName);
        try {
            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            log.info("查询上传后的分片数据:{}",partResult);
            List<Integer> collect = partResult.result().partList().stream().map(Part::partNumber).collect(Collectors.toList());
            log.info("查询上传后的分片索引:{}",collect);
            return collect;
        } catch (Exception e) {
            log.error("error message: 查询上传后的分片信息失败、原因:", e);
            return null;
        }
    }


    /**
     * 初始化分片上传
     *
     * @param fileUploadInfo
     * @return Mono<Map < String, Object>>
     */
    public Map<String, Object> initMultiPartUpload(FileUploadInfo fileUploadInfo) {
        log.info("minioUtil  初始化上传: 通过 <{}> 开始初始化<分片上传>数据", fileUploadInfo);
        String objectName = this.getObjectName(fileUploadInfo.getFileMd5(), fileUploadInfo.getFileName());
        Integer chunkNum = fileUploadInfo.getChunkNum();
        String contentType = fileUploadInfo.getContentType();
        String bucketName = minioConfigProperties.getBucketName();
        // 返回数据
        Map<String, Object> resMap = new HashMap<>();
        try {
            if (StringUtils.isBlank(contentType)) {
                // 没有文件类型，默认是application/octet-stream,默认是下载
                contentType = "application/octet-stream";
            }
            HashMultimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", contentType);

            //获取uploadId
            String uploadId = null;
            if (StringUtils.isBlank(fileUploadInfo.getUploadId())) {
                uploadId = customMinioClient.initMultiPartUpload(bucketName, null, objectName, headers, null);
            } else {
                uploadId = fileUploadInfo.getUploadId();
            }

            resMap.put("uploadId", uploadId);
            fileUploadInfo.setUploadId(uploadId);

            // 获取分片上传地址
            List<String> partList = new ArrayList<>();
            Map<String, String> reqParams = new HashMap<>();
            for (int i = 1; i <= chunkNum; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                // 加入uploadId
                reqParams.put("uploadId", uploadId);
                String uploadUrl = customMinioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(expiry, TimeUnit.DAYS)
                                .extraQueryParams(reqParams)
                                .build());
                partList.add(uploadUrl);
            }
            log.info("tip message: 文件初始化成功");
            resMap.put("urlList", partList);
            resMap.put("code", 200);
            return resMap;
        } catch (Exception e) {
            log.error("error message: 初始化分片上传失败、原因:", e);
            // 返回 文件上传失败
            return null;
        }
    }

    //根据文件的md5和filename获取文件的真实文件名
    public String getObjectName(String fileMd5, String fileName) {
        String suffix = FileUtil.getFileSuffix(fileName);
        log.info("object为：{}", fileMd5 + "." + suffix);
        return fileMd5 + "." + suffix;
    }


    /**
     * 分片上传完后合并
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return boolean
     */
    public boolean mergeMultipartUpload(String objectName, String uploadId, String bucketName) {
        try {
            log.info("tip message: 通过 <{}-{}-{}> 合并<分片上传>数据", objectName, uploadId, bucketName);

            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            log.info("查询上传后的分片数据:{}", partResult);

            if (partResult.result().partList() == null || partResult.result().partList().isEmpty()) {
                log.error("未找到任何分片数据，uploadId: {}", uploadId);
                return false;
            }

            // 根据实际分片数量创建Part数组
            List<Part> partList = partResult.result().partList();
            Part[] parts = new Part[partList.size()];

            // 使用原始分片编号
            for (int i = 0; i < partList.size(); i++) {
                Part part = partList.get(i);
                parts[i] = new Part(part.partNumber(), part.etag());
            }

            log.info("分片信息：{},分片数量：{}", parts, parts.length);
            // 合并分片
            customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
            return true;

        } catch (Exception e) {
            log.error("error message: 合并失败、原因:", e);
            return false;
        }
    }


    /**
     * 获取文件完整的地址
     *
     * @param bucketName 桶名称
     * @param objectName 文件名
     * @return
     */
    public String getFilePath(String bucketName, String objectName) {
        return minioConfigProperties.getEndpoint() + "/" + bucketName + "/" + objectName;//文件访问路径
    }

    public String uploadFileByte(String serviceName, String object, ByteArrayOutputStream byteArrayOutputStream) {
        // 检查 byteArrayOutputStream 是否为空
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() == 0) {
            LogUtil.Error("上传失败：byteArrayOutputStream 为空或没有数据", new Exception("byteArrayOutputStream 为空或没有数据"));
            return null;
        }
        String theCurrentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        object = serviceName + "/" + theCurrentTime + "/" + object;
        // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try {
            customMinioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfigProperties.getBucketName())
                            .object(object)
                            // 设置文件为预览
                            .contentType("application/pdf")
                            .stream(byteArrayInputStream, byteArrayOutputStream.size(), -1)
                            .build());
            return getFilePath(minioConfigProperties.getBucketName(), object);
        } catch (Exception e) {
            LogUtil.Error("文件上传失败", e);
            return null;
        }
    }
}

package weilai.team.officialWebSiteApi.service.recruit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.recruit.DO.Files;
import weilai.team.officialWebSiteApi.mapper.recruit.FilesMapper;
import weilai.team.officialWebSiteApi.service.recruit.FileDownloadService;
import weilai.team.officialWebSiteApi.util.MinioUtil;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载服务实现类
 */
@Service
public class FileDownloadServiceImpl implements FileDownloadService {

    private static final Logger log = LoggerFactory.getLogger(FileDownloadServiceImpl.class);

    @Resource
    private FilesMapper filesMapper;

    @Resource
    private MinioUtil minioUtil;

    /**
     * 初始化分片下载
     * 根据 fileMd5 从数据库查询文件信息，然后生成预签名 URL 列表
     *
     * @param fileMd5 文件的 MD5 值
     * @return 包含分片 URL、文件名、文件大小等信息
     */
    @Override
    public ResponseResult<?> initMultipartDownload(String fileMd5) {
        log.info("初始化分片下载: fileMd5={}", fileMd5);

        try {
            // 1. 从数据库查询文件信息
            Files file = filesMapper.selectOne(
                    new LambdaQueryWrapper<Files>()
                            .eq(Files::getFileMd5, fileMd5)
                            .eq(Files::getIsDelete, false)
                            .eq(Files::getEnable, true)
            );

            if (file == null) {
                log.warn("文件不存在: fileMd5={}", fileMd5);
                return ResponseResult.DOWNLOAD_FILE_NOT_FOUND;
            }

            // 2. 从 MinIO 获取文件实际大小（确保准确性）
            String objectName = minioUtil.getObjectName(file.getUrl());
            Map<String, Object> fileInfo = minioUtil.getFileInfo(objectName);

            if (fileInfo == null) {
                log.error("无法获取文件信息: objectName={}", objectName);
                return ResponseResult.DOWNLOAD_INIT_FAIL;
            }

            Long fileSize = (Long) fileInfo.get("fileSize");
            String fileName = file.getFileName();

            // 3. 生成分片下载的预签名 URL
            // 使用数据库中保存的 chunkSize，如果为空则使用默认值 5MB
            Long chunkSize = file.getChunkSize();
            Map<String, Object> downloadUrls = minioUtil.generateMultipartDownloadUrls(
                    objectName, 
                    fileSize, 
                    chunkSize
            );

            if (downloadUrls == null) {
                log.error("生成分片下载URL失败: objectName={}", objectName);
                return ResponseResult.DOWNLOAD_INIT_FAIL;
            }

            // 4. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileSize", fileSize);
            result.put("urlList", downloadUrls.get("urlList"));
            result.put("chunkNum", downloadUrls.get("chunkNum"));
            result.put("chunkSize", downloadUrls.get("chunkSize"));
            result.put("fileMd5", fileMd5);

            log.info("分片下载初始化成功: fileName={}, fileSize={}, chunkNum={}", 
                    fileName, fileSize, downloadUrls.get("chunkNum"));

            return ResponseResult.DOWNLOAD_INIT_SUCCESS.put(result);

        } catch (Exception e) {
            log.error("初始化分片下载异常: fileMd5={}", fileMd5, e);
            return ResponseResult.DOWNLOAD_INIT_FAIL;
        }
    }
}

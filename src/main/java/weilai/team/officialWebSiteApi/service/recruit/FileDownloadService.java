package weilai.team.officialWebSiteApi.service.recruit;

import weilai.team.officialWebSiteApi.util.ResponseResult;

/**
 * 文件下载服务接口
 */
public interface FileDownloadService {

    /**
     * 初始化分片下载，生成预签名 URL 列表
     *
     * @param fileMd5 文件的 MD5 值
     * @return 包含分片 URL、文件名、文件大小等信息
     */
    ResponseResult<?> initMultipartDownload(String fileMd5);
}

package weilai.team.officialWebSiteApi.controller.recruit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.service.recruit.FileDownloadService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 文件下载控制器
 * 提供基于 MinIO 预签名 URL 的分片下载功能
 */
@RestController
@RequestMapping("/download")
@Api(tags = "文件分片下载", description = "基于MinIO预签名URL的大文件分片下载")
@ApiResponses(value = {
        @ApiResponse(code = 3420, message = "分片下载初始化成功"),
        @ApiResponse(code = 3421, message = "分片下载初始化失败"),
        @ApiResponse(code = 3422, message = "下载文件不存在")
})
public class FileDownloadController {

    @Resource
    private FileDownloadService fileDownloadService;

    /**
     * 初始化分片下载
     * 根据文件 MD5 获取文件信息，并生成所有分片的预签名 URL
     *
     * @return 包含文件名、文件大小、分片 URL 列表等信息
     */
    @PostMapping("/multipart/init")
    @ApiOperation("初始化分片下载，获取预签名URL列表")

    public ResponseResult<?> initMultipartDownload(@RequestBody Map<String, String> request) {
        String fileMd5 = request.get("fileMd5");
        
        if (fileMd5 == null || fileMd5.trim().isEmpty()) {
            return ResponseResult.PARAM_IS_NOT_VALID;
        }
        
        return fileDownloadService.initMultipartDownload(fileMd5);
    }
}

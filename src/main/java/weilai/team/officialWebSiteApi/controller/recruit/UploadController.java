package weilai.team.officialWebSiteApi.controller.recruit;
import io.minio.SetBucketPolicyArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.recruit.DO.CustomMinioClient;
import weilai.team.officialWebSiteApi.entity.recruit.DO.FileUploadInfo;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.MergerFileDTO;
import weilai.team.officialWebSiteApi.service.recruit.FileService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.validation.Valid;
/**
 * @author lzw
 * @date 2025/2/18 8:03
 * @description 作用：文件上传
 */
@RestController
@RequestMapping("/upload")
@ApiResponses(
        value = {
                @ApiResponse(code = 3400,message = "文件存在，无需上传"),
                @ApiResponse(code = 3401,message = "文件正在上传"),
                @ApiResponse(code = 3402,message = "文件合并成功"),
                @ApiResponse(code = 3403,message = "分片上传初始化成功"),
                @ApiResponse(code = 3410,message = "文件不存在，请上传"),
                @ApiResponse(code = 3411,message = "文件合并失败"),
                @ApiResponse(code = 3412,message = "分片上传初始化失败"),
        }
)
@Api(tags = "文件分片上传", description = "")
public class UploadController {

    @Resource
    private FileService fileService;


    /**
     * 校验文件是否存在
     * @param fileMd5 String
     * @return true：存在  false：不存在
     */
    @GetMapping("/multipart/check/{fileMd5}")
    @ApiOperation("第一步：判断数据库或redis中是不是已经存在，是否要进行上传")
    public ResponseResult<?> checkFile(@PathVariable("fileMd5") String fileMd5) {
        return fileService.getFileByMD5(fileMd5);
    }

    /**
     * 分片初始化
     * @param fileUploadInfo 文件信息
     * @return ResponseResult<Object>
     */
    @PostMapping("/multipart/init")
    @ApiOperation("第二步 ：初始化文件上传,返回每一片要上传的地址")
    public ResponseResult<?> initMultiPartUpload(@RequestBody @Valid FileUploadInfo fileUploadInfo) {
        LogUtil.info("second ------------------------>  初始化文件上传" +  fileUploadInfo);
        return fileService.initMultiPartUpload(fileUploadInfo);
    }


    /**
     * 完成上传
     * @return ResponseResult<Object>
     */
    @PostMapping("/multipart/merge")
    @ApiOperation("第三步：合并上传任务")
    public ResponseResult<?> completeMultiPartUpload(@RequestBody MergerFileDTO mergerFileDTO) {
        LogUtil.info("third ------------------------》 合并上传任务" + mergerFileDTO);
        //合并文件
        return fileService.mergeMultipartUpload(mergerFileDTO);
    }


}

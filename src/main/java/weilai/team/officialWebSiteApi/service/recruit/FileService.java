package weilai.team.officialWebSiteApi.service.recruit;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.recruit.DO.FileUploadInfo;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.MergerFileDTO;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileDirEnum;
import weilai.team.officialWebSiteApi.util.ResponseResult;
/**
 * @author lzw
 * @date 2024/11/11 16:18
 * @description 作用：文件上传的业务
 */
public interface FileService {

    /**
     * 完成分片上传
     *
     * @param
     */
    ResponseResult<?> mergeMultipartUpload(MergerFileDTO mergerFileDTO);


    /**
     *  通过 md5 获取已上传的数据
     * @param fileMd5 String
     * @return 返回
     */
    ResponseResult<?> getFileByMD5(String fileMd5);


    /**
     * 作用：上传文件,调用方在service里进行参数校验
     * @param file 要上传的文件
     * @param fileDirEnum 文件存放的目录，枚举类
     * @return 文件的访问路径
     */
    String uploadFile(MultipartFile file, FileDirEnum fileDirEnum,String filePrefixName,String type);




    /**
     * excel文件导入，要求文件必须是excel文件
     * @param file  要导入的文件
     * @return 返回结果
     */
    ResponseResult<?> uploadExcelFile(MultipartFile file);



    /**
     * 分片上传初始化
     *
     * @param fileUploadInfo
     * @return Map<String, Object>
     */
    ResponseResult<?> initMultiPartUpload(FileUploadInfo fileUploadInfo);

}

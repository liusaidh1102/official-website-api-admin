package weilai.team.officialWebSiteApi.entity.recruit.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lzw
 * @date 2025/2/17 8:57
 * @description 作用：
 */

@Data
@Accessors(chain = true)
@ApiModel(description = "文件上传信息")
public class FileUploadInfo {

    @NotBlank(message = "文件名不能为空")
    @ApiModelProperty(value = "文件名带后缀")
    private String fileName;

    @ApiModelProperty(value = "总文件大小（字节）")
    private Long fileSize;

    @ApiModelProperty(value = "文件的contentType")
    private String contentType;

    @NotNull(message = "分片数量不能为空")
    @ApiModelProperty(value = "分片总数量")
    private Integer chunkNum;

    @ApiModelProperty(value = "uploadId,可以为空")
    private String uploadId;

    @ApiModelProperty(value = "每个分片的大小(单位：字节)")
    private Long chunkSize;

    @NotBlank(message = "fileMd5 不能为空")
    @ApiModelProperty(value = "文件的md5，不能为null")
    private String fileMd5;

    @ApiModelProperty(value = "文件的类型，可以为null")
    private String fileType;

    @ApiModelProperty(value = "文件url")
    private String url;

    //已上传的分片索引+1
    @ApiModelProperty(value = "分片上传的索引，可以为null")
    private List<Integer> chunkUploadedList;

}

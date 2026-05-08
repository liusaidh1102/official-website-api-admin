package weilai.team.officialWebSiteApi.entity.recruit.DO;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* 面试支持的班级
* @TableName recruit_class
*/
@TableName("recruit_class")
@Data
public class RecruitClass implements Serializable {

    /**
    * ID，主键自增
    */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("ID，主键自增")
    private Long id;
    /**
    * 年级
    */
    @ApiModelProperty("年级")
    private String grade;
    /**
    * 班级
    */
    @ApiModelProperty("班级")
    private String clazz;
    /**
    * 添加的时间
    */
    @ApiModelProperty("添加的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
    * 是否删除，0表示未删除，1表示已删除
    */
    @ApiModelProperty("是否删除，0表示未删除，1表示已删除")
    @TableField("is_deleted")
    private Integer isDeleted;

}

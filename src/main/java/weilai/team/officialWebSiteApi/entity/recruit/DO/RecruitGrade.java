package weilai.team.officialWebSiteApi.entity.recruit.DO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RecruitGrade implements Serializable {


    /*
    id,主键自增
     */
    private Integer id;
    /**
    * 年级
    */
    private String grade;
    /**
    * 时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}

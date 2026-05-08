package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import weilai.team.officialWebSiteApi.util.Values;

import javax.validation.constraints.NotBlank;

/**
 * @author lzw
 * @date 2024/12/1 8:44
 * @description 作用： 用户信息导入模板
 */
@Data
@ApiModel("信息导入表格")
public class UserInfoExcel {
    /**
     * 姓名 <= 5
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 学号<= 15
     */
    @ExcelProperty("学号")
    private String studyId;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    private String email;


    /**
     * 性别
     */
    @ExcelProperty("性别")
    private String sex;

    /**
     * 班级<= 10
     */
    @ExcelProperty("班级")
    private String clazz;

    /**
     * 年级 <= 5
     */
    @ExcelProperty("年级")
    private String grade;

    /**
     * 组别<= 5
     */
    @ExcelProperty("组别")
    private String group;




    /**
     * 手机号
     */
    @ExcelProperty("电话")
    private String phone;

    /**
     * qq号
     */
    @ExcelProperty("QQ")
    private String qq;

    /*
    钉钉号
     */
    private String dingUserId;


    /**
     * 密码
     */
    private String password;

    /**
     * 学号[账号]
     */
    private String username;

}

package weilai.team.officialWebSiteApi.entity.recruit.VO;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lzw
 * @date 2024/11/22 9:51
 * @description 作用： 结果的导出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@HeadStyle(fillPatternType  = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 49)
public class RecruitResultExcelVO {

    @ExcelProperty({"面试结果","姓名"})
    private String name;

    @ExcelProperty({"面试结果","年级"})
    private String grade;

    @ExcelProperty({"面试结果","班级"})
    private String clazz;

    @ExcelProperty({"面试结果","性别"})
    private String sex;

    @ExcelProperty(value = {"面试结果","日期"})
    private String time;

    @ExcelProperty({"面试结果","录取状态 "})
    String status;

    public void setTime(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.time = date.format(formatter);
    }

    public RecruitResultExcelVO(String name, String grade, String clazz, String sex, LocalDateTime updateTime, Integer status) {
        this.setTime(updateTime.toLocalDate());
        this.name = name;
        this.grade = grade;
        this.clazz = clazz;
        this.sex = sex;
//        0代表待安排；1代表待面试；2代表已录取；3代表未录取
        if (status.equals(0)){
            this.status = "待安排";
        } else if (status.equals(1)) {
            this.status = "待面试";
        } else if (status.equals(2)) {
            this.status = "已录取";
        }else {
            this.status = "未录取";
        }
    }
}

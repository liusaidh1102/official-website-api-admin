package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.List;

/**
 * ClassName:SummarizeVO
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/8 21:17
 */
@Data
public class SummarizeVO {
    // 总人数
    private Integer allTeamUserCount;

    // 总平均薪资
    private Integer salaryYearAll;

    // 软件著作权数
    private Integer copyrightCount;

    // 蓝桥杯数
    private Integer lanQiaoCount;

    // 当前后端人数
    private Integer javaCurrent;

    // 当前前端人数
    private Integer htmlCurrent;

    // 当前考研人数
    private Integer examCurrent;

    // 当前实习人数
    private Integer workCurrent;

    // 拥有账号的总人数
    private Integer userCount;

    // 博客数
    private Integer postB;

    // 公告数
    private Integer postG;

    // 交流数
    private Integer postJ;

    // 头脑风暴数
    private Integer postT;

    // 后端总人数
    private Integer javaAll;

    // 前端总人数
    private Integer htmlAll;

    // 每届前端、后端的信息
    private List<Per> perByGrade;

    // 不同地区的人数
    private List<Area> areas;

    @Data
    public static class Per {
        // 年级
        private Integer grade;
        // 前端平均年薪
        private Double salaryYearPerHtml;
        // 后端平均年薪
        private Double salaryYearPerJava;
        // 前端人数
        private Integer PeopleCountPerHtml;
        // 后端人数
        private Integer PeopleCountPerJava;

        public void setGrade(String grade){
            if(grade == null) {
                this.grade = 0;
            } else {
                this.grade = Integer.parseInt(grade);
            }
        }
    }

    @Data
    public static class Area {
        // 地区的邮政编码
        private Integer zipcode;

        // 人的数量
        private Integer peopleCount;

    }
}

package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:CheckInfoSingleVO
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/12 22:55
 */
@Data
public class CheckInfoSingleVO {
    public CheckInfoSingleVO(long userCountShould, int userCountCurrent,
                             int leavesUserCount, int lateUserCount,
                             Map<String, Integer[]> mapBiao,
                             Map<Date, Integer[]> mapDaw,Map<String, List<String>> detail) {
        this.userCountShould = userCountShould;
        this.userCountCurrent = userCountCurrent;
        this.leavesUserCount = leavesUserCount;
        this.lateUserCount = lateUserCount;
        this.teamTable = mapBiao;
        this.lineChart = mapDaw;
        this.detail = detail;
    }

    long userCountShould; // 应到人数 + 折线图的应到人数
    int userCountCurrent; // 实到人数
    int leavesUserCount; // 请假人数
    int lateUserCount; // 迟到人数
    Map<String,Integer[]> teamTable; // 分组的表格图
    Map<Date,Integer[]> lineChart; // 折现图
    Map<String, List<String>> detail; // 详情人名单
}

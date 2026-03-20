package weilai.team.officialWebSiteApi.entity.admin.VO;

import com.dingtalk.api.response.OapiAttendanceListResponse;
import com.taobao.api.internal.mapping.ApiField;
import lombok.Data;

import java.util.Date;

/**
 * ClassName:CheckInfoVO
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/11 22:46
 */
@Data
public class CheckInfoVO {

    public CheckInfoVO(OapiAttendanceListResponse.Recordresult recordresult,String name,String group){
        if(recordresult != null) {
            this.baseCheckTime = recordresult.getBaseCheckTime();
            this.checkType = recordresult.getCheckType();
            this.timeResult = recordresult.getTimeResult();
            this.userCheckTime = recordresult.getUserCheckTime();
            this.userId = recordresult.getUserId();
            this.workDate = recordresult.getWorkDate();
        }
        this.name = name;
        this.group = group;
    }

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户组别
     */
    private String group;

    /**
     * 应打卡时间
     */
    private Date baseCheckTime;

    /**
     * 打卡类型：
     * OnDuty：上班
     * OffDuty：下班
     */
    private String checkType;

    /**
     * 打卡结果：
     * Normal：正常
     * Late：迟到
     * Early：早退
     * Absenteeism：旷工
     * NotSigned：未打卡
     * SeriousLate：严重迟到
     */
    private String timeResult;

    /**
     * 用的实际打卡时间
     */
    private Date userCheckTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 工作日期
     */
    private Date workDate;
}

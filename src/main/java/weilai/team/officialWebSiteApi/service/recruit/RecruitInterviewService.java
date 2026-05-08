package weilai.team.officialWebSiteApi.service.recruit;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitCommentDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitInterviewDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ScheduleInterviewDTO;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.servlet.http.HttpServletRequest;
/**
 * @author lzw
 * @date 2024/11/11 15:07
 * @description 作用：招新的面试相关业务
 */
@Service
public interface RecruitInterviewService{

    /**
     * 安排面试官
     * @param scheduleInterviewDTO 参数
     * @return 返回结果
     */
    ResponseResult<?> scheduleInterviewer(ScheduleInterviewDTO scheduleInterviewDTO);

    /**
     * 面试官评价
     * @param recordDTO 参数
     * @return 返回结果
     */
    ResponseResult<?> insertComment(RecruitCommentDTO recordDTO);

    /**
     * 获取所有的面试人员
     * @param interviewDTO 查询的参数
     * @return 返回结果
     */
    ResponseResult<?> listAllInterview(RecruitInterviewDTO interviewDTO, HttpServletRequest request);

    /**
     * 获取所有的面试官
     * @param pageDTO 分页参数
     * @param name 姓名
     * @return 返回结果
     */
    ResponseResult<?> listAllHr(PageDTO pageDTO,String name);

    /**
     * 获取评价
     * @param id 面试记录的id
     * @return 返回结果
     */
    ResponseResult<?> getComment(Integer id);
}

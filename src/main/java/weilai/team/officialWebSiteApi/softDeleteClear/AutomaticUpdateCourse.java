package weilai.team.officialWebSiteApi.softDeleteClear;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.mapper.admin.UserCourseMapper;
import weilai.team.officialWebSiteApi.service.admin.UserService;

import javax.annotation.Resource;

/**
 * ClassName:AutomaticUpdateCourse
 * Description:
 *
 * @Author:独酌
 * @Create:2025/9/1 19:52
 */
@Slf4j
@Component
public class AutomaticUpdateCourse {

    @Resource
    private UserService userService;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Async //表示开启异步任务
    @Scheduled(cron = "0 0 2 * * ?") // [cron表达式：每天凌晨 2 点]
    public void userCourseClear(){
        log.info("定时任务开始执！清理用户课程表");
        int i = userCourseMapper.clearUserCourse();
        log.info("定时任务结束！清理 {} 个用户课程",i);
        log.info("定时任务开始执！自动更新用户课程表");
        int i1 = userService.AutomaticUpdateUserCourse();
        log.info("定时任务结束！已自动更新 {} 个用户课程",i1);
    }
}

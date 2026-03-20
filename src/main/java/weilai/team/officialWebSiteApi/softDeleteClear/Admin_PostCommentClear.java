package weilai.team.officialWebSiteApi.softDeleteClear;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.mapper.admin.UserCourseMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentAllMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentOneMapper;
import javax.annotation.Resource;

/**
 * ClassName:SaftDeleteClear
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/5 16:22
 */
@Component
@Slf4j
public class Admin_PostCommentClear {

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostCommentOneMapper postCommentOneMapper;

    @Resource
    private PostCommentAllMapper postCommentAllMapper;

    @Async //表示开启异步任务
    @Scheduled(cron = "0 0 3 ? * MON") // [cron表达式：每周一 3点 0分 0秒 执行！]
    public void userCourseClear(){
        log.info("定时任务开始执！清理用户的课程表！");
        int i = userCourseMapper.softDeleteClear();
        log.info("定时任务结束！被清理用户的课程表数量为：{}",i);
    }

    @Async //表示开启异步任务
    @Scheduled(cron = "0 5 3 ? * MON") // [cron表达式：每周一 3点 5分 0秒 执行！]
    public void userClear(){
        log.info("定时任务开始执！清理用户！");
        int i = userMapper.softDeleteClear();
        log.info("定时任务结束！被清理的用户数量为：{}",i);
    }

    @Async //表示开启异步任务
    @Scheduled(cron = "0 10 3 ? * MON") // [cron表达式：每周一 3点 10分 0秒 执行！]
    public void postCommentOneClear(){
        log.info("定时任务开始执！开始清理一级评论！");
        int i = postCommentOneMapper.softDeleteClear();
        log.info("定时任务结束！被清理的一级评论的数量为：{}",i);
    }

    @Async //表示开启异步任务
    @Scheduled(cron = "0 15 3 ? * MON") // [cron表达式：每周一 3点 15分 0秒 执行！]
    public void postCommentAllClear(){
        log.info("定时任务开始执！开始清理多级评论！");
        int i = postCommentAllMapper.softDeleteClear();
        log.info("定时任务结束！被清理的多级评论的数量为：{}",i);
    }
}

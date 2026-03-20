package weilai.team.officialWebSiteApi.softDeleteClear;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.mapper.post.UserCollectMapper;

@Component
@Slf4j
public class Admin_PostClear {
     @Autowired
    private PostMapper postMapper;
     @Autowired
     private UserCollectMapper userCollectMapper;

    @Async //表示开启异步任务
    @Scheduled(cron = "0 0 1 ? * MON") // [cron表达式：每周一 1点 0分 0秒 执行！]
    public void userCourseClear(){
        log.info("定时任务开始执！清理帖子");
       int effectRow= postMapper.clearPost();
       log.info("清理帖子成功，共清理了{}条数据",effectRow);
    }
    @Async //表示开启异步任务
    @Scheduled(cron = "0 5 1 ? * MON")// [cron表达式：每周一 1点 5分 0秒 执行！]
    public void userCollectClear(){
        log.info("定时任务开始执！清理收藏垃圾");
        int effectRow= userCollectMapper.clearUserCollectPost();
        log.info("清理收藏垃圾成功，共清理了{}条数据",effectRow);
    }
}

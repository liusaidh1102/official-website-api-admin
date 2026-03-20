package weilai.team.officialWebSiteApi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("weilai.team.officialWebSiteApi.mapper")
@EnableScheduling /// 开启定时任务支持
@EnableAsync /// 开启异步任务支持
public class OfficialWebsiteApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficialWebsiteApiApplication.class, args);
    }

}

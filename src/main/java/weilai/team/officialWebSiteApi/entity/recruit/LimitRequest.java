package weilai.team.officialWebSiteApi.entity.recruit;

import java.lang.annotation.*;

//请求限制，指定请求次数
@Documented
@Target( ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest {
    long time() default 6000; // 限制时间 单位：毫秒
    int count() default 1; // 允许请求的次数
}

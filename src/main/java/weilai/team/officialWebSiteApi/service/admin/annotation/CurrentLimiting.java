package weilai.team.officialWebSiteApi.service.admin.annotation;

import java.lang.annotation.*;

/**
 * 主要用于实现特定接口的限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentLimiting {
    int limitingPolicy() default 10; // 单位时间内请求的数量，默认 10 个请求
    int timePolicy() default 5; // 单位时间，默认 5 秒
}

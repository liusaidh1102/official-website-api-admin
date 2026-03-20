package weilai.team.officialWebSiteApi.service.admin.annotation;

import java.lang.annotation.*;

/**
 * 通用日志注解，专用于 admin 类，目前并未推广
 */
@Target(ElementType.METHOD) // 表示修饰方法
@Retention(RetentionPolicy.RUNTIME) // 注解保留到运行时（AOP 需要反射获取）
@Documented // 生成文档时包含该注解
public @interface AutoLog {

}

package weilai.team.officialWebSiteApi.service.admin.annotation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.service.admin.annotation.CurrentLimiting;
import weilai.team.officialWebSiteApi.util.RedisUtilPlus;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static weilai.team.officialWebSiteApi.util.Values.LIMITING_PREFIX;

@Aspect
@Order(1)
@Component
public class CurrentLimitingAspect {

    @Resource
    RedisUtilPlus redisUtilPlus;

    private static final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();


    @Pointcut("@annotation(weilai.team.officialWebSiteApi.service.admin.annotation.CurrentLimiting)")
    public void CurrentLimiting() {}

    @Around("CurrentLimiting()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取 @CurrentLimiting 注解的属性（如操作描述）
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CurrentLimiting currentLimiting = method.getAnnotation(CurrentLimiting.class);
        int count = currentLimiting.limitingPolicy();
        int time = currentLimiting.timePolicy();
        String name = method.getName();
        Object lock = locks.computeIfAbsent(LIMITING_PREFIX + name, k -> new Object());
        synchronized(lock) {
            Boolean absent = redisUtilPlus.STRING.setIfAbsent(LIMITING_PREFIX + name, String.valueOf(count),time, TimeUnit.SECONDS);
            System.out.println("absent: " + absent + ", ttl: " + redisUtilPlus.REDIS.getExpire(LIMITING_PREFIX + name,TimeUnit.SECONDS));
            if(Boolean.FALSE.equals(absent)) {
                // 说明已经存在了,因此这里判断是否能正常请求
                Long increment = redisUtilPlus.STRING.increment(LIMITING_PREFIX + name, -1);
                // 关键：检查 TTL，如果异常就补设过期时间
                Long currentTtl = redisUtilPlus.REDIS.getExpire(LIMITING_PREFIX + name);
                if (currentTtl == null || currentTtl < 0) {
                    redisUtilPlus.REDIS.expire(LIMITING_PREFIX + name, time, TimeUnit.SECONDS); // 重新设过期时间
                }
                if(increment == null || increment < 0) {
                    return ResponseResult.SERVICE_BUSY.put("请 5 秒后重试");
                }
            }
        }
        // 实际方法的执行并返回结果
        return joinPoint.proceed();
    }
}

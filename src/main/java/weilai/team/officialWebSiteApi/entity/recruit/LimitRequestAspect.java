package weilai.team.officialWebSiteApi.entity.recruit;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//自定义aop
@Aspect
@Component
public class LimitRequestAspect {

    private static final Logger log = LoggerFactory.getLogger(LimitRequestAspect.class);
    private static ConcurrentHashMap<String, ExpiringMap<String, Integer>> book = new ConcurrentHashMap<>();

    // 定义切点
    // 让所有有@LimitRequest注解的方法都执行切面方法
    @Pointcut("@annotation(limitRequest)")
    public void excuseService(LimitRequest limitRequest) {
    }

    @Around(value = "excuseService(limitRequest)", argNames = "pjp,limitRequest")
    public Object doAround(ProceedingJoinPoint pjp, LimitRequest limitRequest) throws Throwable {

        // 获得request对象
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        // 获取Map对象， 如果没有则返回默认值
        // 第一个参数是key， 第二个参数是默认值
        ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        // 获取ip地址
        String remoteAddr = request.getRemoteAddr();
        // 获取当前ip请求的次数
        Integer uCount = uc.getOrDefault(remoteAddr, 0);
        log.info("当前ip为：{}  当前次数为：{}", remoteAddr, uCount);

        if (uCount >= limitRequest.count()) { // 超过次数，不执行目标方法
            return ResponseResult.REQUEST_LIMIT;
        } else if (uCount == 0){ // 第一次请求时，设置有效时间
            uc.put(request.getRemoteAddr(), uCount + 1, ExpirationPolicy.CREATED, limitRequest.time(), TimeUnit.MILLISECONDS);
        } else { // 未超过次数， 记录加一
            uc.put(request.getRemoteAddr(), uCount + 1);
        }
        book.put(request.getRequestURI(), uc);

        // result的值就是被拦截方法的返回值

        return pjp.proceed();
    }


}
package weilai.team.officialWebSiteApi.service.admin.annotation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Order(2)
@Component
public class LogAspect {

    // 通过 @Pointcut 注解
    // @annotation(...)	按方法是否被指定注解标记匹配
    // execution(...)	按方法的类路径、返回值、参数匹配
    @Pointcut("@annotation(weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog)")
    public void AutoLog() {} // 本身不执行任何业务逻辑，它的核心价值是给切入点表达式起一个可复用的名字

    // 用于打印日志
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    // 参数相关的颜色常量
    private static final String ANSI_RESET = "\u001B[0m";    // 重置颜色
    private static final String ANSI_CYAN = "\u001B[36m";    // 青色文本（用于参数名）
    private static final String ANSI_PURPLE = "\u001B[35m";  // 紫色文本（用于参数值）
    private static final String ANSI_BLUE = "\u001B[34m";     // 蓝色文本
    private static final String ANSI_GREEN = "\u001B[32m";   // 绿色文本
    private static final String ANSI_YELLOW = "\u001B[33m";  // 黄色文本

    // 环绕通知：拦截并增强匹配的方法
    @Around("AutoLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String name = signature.getName();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameters = joinPoint.getArgs();

        // 获取 @AutoLog 注解的属性（如操作描述）
        // Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // AutoLog autoLog = method.getAnnotation(AutoLog.class);

        // 执行增强逻辑（如记录开始时间）
        long startTime = System.currentTimeMillis();

        // 执行目标方法（业务逻辑）
        Object result = joinPoint.proceed();

        // 执行后置增强（如记录结束时间）
        long endTime = System.currentTimeMillis();

        String message = "";
        if(result instanceof ResponseResult<?>)
            message = ((ResponseResult<?>) result).getMessage();

        // 修改后的输出代码
        bw.write(ANSI_BLUE + time + ANSI_RESET + "【"
                + ANSI_GREEN + name + ANSI_RESET + "】 耗时："
                + ANSI_YELLOW + (endTime - startTime) + "ms" + ANSI_RESET + ANSI_RESET
                + "，返回结果：" + ANSI_CYAN + message + ANSI_RESET);


        bw.write(" ");

        // 修改后的开始执行日志代码
        bw.write(" 参数：");

        for(int i = 0; i < parameters.length; i++) {
            bw.write(ANSI_CYAN + parameterNames[i] + ANSI_RESET + ": "
                    + ANSI_PURPLE + parameters[i] + ANSI_RESET);
            if(i != parameters.length - 1) bw.write(", ");
        }

        // 加一个换行
        bw.write("\n");

        bw.flush();
        return result;
    }
}

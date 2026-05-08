package weilai.team.officialWebSiteApi.config;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.WebUtil;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:GlobalExceptionHandler
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/23 10:09
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MysqlDataTruncation.class)
    public void handleException(MysqlDataTruncation e, HttpServletResponse response) {
        WebUtil.sendResponse(response,ResponseResult.Bad_Request.put("数据过长，写入失败"),"application/json;charset=UTF-8");
    }

    @ExceptionHandler(BindException.class)
    public void handleException(BindException e, HttpServletResponse response) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        WebUtil.sendResponse(response,ResponseResult.Bad_Request.put(errors),"application/json;charset=UTF-8");
    }
}

package weilai.team.officialWebSiteApi.util;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletResponse;

public class WebUtil {
    public static <T> void sendResponse(HttpServletResponse response,T data,String contentType){
        response.reset();
        response.setContentType(contentType/*"application/json;charset=UTF-8"*/);
        /*手动配置跨域问题*/
        // 设置允许跨域请求的域名，* 表示允许所有域名
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 设置允许的请求方法
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        // 设置允许的请求头
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 是否允许发送 Cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 预检请求的缓存时间（秒）
        response.setHeader("Access-Control-Max-Age", "3600");
        try {
            String json = JSON.toJSONString(data);
            response.getWriter().println(json);
            response.flushBuffer();
        } catch (Exception e) {
            LogUtil.Error("响应体写入错误！",e);
        }
    }
}

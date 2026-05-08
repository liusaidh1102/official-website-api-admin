package weilai.team.officialWebSiteApi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class SpringBootCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //设置允许跨域的路径 == 所有路径都允许跨域
                .allowedOriginPatterns("*") //设置允许跨域请求域名（ip） == 所有域名都允许
                .allowCredentials(true) //是否允许cookie
                .allowedMethods("GET","POST","DELETE","PUT") //设置允许的请求方式
                .allowedHeaders("*") //设置允许的header属性
                .maxAge(3600); //跨域允许时间
    }
}

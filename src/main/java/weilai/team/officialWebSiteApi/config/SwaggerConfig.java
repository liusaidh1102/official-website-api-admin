package weilai.team.officialWebSiteApi.config;

import springfox.documentation.service.*;
import springfox.documentation.builders.RequestHandlerSelectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import weilai.team.officialWebSiteApi.util.Values;

import java.util.List;
import static java.util.Collections.singletonList;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        // 创建一个 swagger 的 bean 实例
        String tokenParamName = Values.TOKEN_PARAM_NAME;
        return new Docket(DocumentationType.SWAGGER_2)
                //设置分组名
                .groupName("official-website-api")

                // 是否开启 swagger：true -> 开启，false -> 关闭
                .enable(true)

                // 配置基本信息
                .apiInfo(apiInfo())

                // 配置接口信息，配置如何扫描接口
                .select() //设置扫描接口
                .apis(RequestHandlerSelectors
                                .basePackage("weilai.team.officialWebSiteApi.controller") // 扫描指定包下的接口，最为常用
                ).build()

                //token配置
                .securityContexts(singletonList(securityContext()))
                // ApiKey的name需与SecurityReference的reference保持一致
                .securitySchemes(singletonList(new ApiKey("安全令牌", tokenParamName, "header")));
    }


    /**
     * token 配置
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    /**
     * token 配置
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "作用域为全局");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        //SecurityReference 保持一致  描述
        return singletonList(new SecurityReference("安全令牌", authorizationScopes));
    }


    /**
     * 基本信息配置
     */
    // 基本信息设置
    private ApiInfo apiInfo() {
        Contact contact = new Contact("未来23届", // 作者姓名
                "https://baidu", // 作者网址
                "作者邮箱"); // 作者邮箱

        return new ApiInfoBuilder()
                .title("未来官网接口") // 标题
                .description("众里寻他千百度，慕然回首那人却在灯火阑珊处") // 描述
                .termsOfServiceUrl("https://www.baidu.com") // 跳转连接
                .version("1.0") // 版本
                .license("Swagger-的使用(详细教程)")
                .licenseUrl("https://blog.csdn.net/xhmico/article/details/125353535")
                .contact(contact)
                .build();
    }
}

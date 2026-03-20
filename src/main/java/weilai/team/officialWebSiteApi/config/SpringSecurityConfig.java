package weilai.team.officialWebSiteApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import weilai.team.officialWebSiteApi.config.securityComponent.DIYAccessDeniedHandler;
import weilai.team.officialWebSiteApi.config.securityComponent.DIYAuthenticationEntryPoint;
import weilai.team.officialWebSiteApi.config.securityComponent.DIYJwtAuthenticationTokenFilter;

import javax.annotation.Resource;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private DIYJwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private DIYAuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    private DIYAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置需要授权的对象
        http.authorizeRequests()
                // 登录
                .antMatchers("/index/logout","/index/getSummarize").authenticated() //登出接口只允许登录访问
                .antMatchers("/index/**","/recruit/user/**").anonymous() //登录接口只允许匿名访问

                //用户的个人信息
                .antMatchers("/user/**").authenticated() //用户信息接口只允许登录访问

                //概括 + 通讯录
                .antMatchers("/userManager/teamInfo/**").hasAuthority("team_admin")

                //权限管理
                .antMatchers("/userManager/permission/**").hasAuthority("admin_plus")

                //社区
                .antMatchers("/post/**","/community_tag/**","/notice/**").authenticated()

                //公告
                .antMatchers("/notice/updateNotice",
                             "/notice/deleteNotice/**",
                             "/notice/batchDeleteNotices").hasAuthority("notice_admin")

                //社区管理
                .antMatchers("/admin_post/**").hasAuthority("community_admin")

                //信息
                .antMatchers("/message/**").authenticated()

                //评论
                .antMatchers("/comment/**").authenticated()

                //招新
                .antMatchers("/recruit/**").hasAuthority("recruit_admin")

                // 考勤
                .antMatchers("/Attendance/**").hasAuthority("attendance_admin")

                //兜底
                .anyRequest().authenticated();


        //配置自定义的过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //配置自定义异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        //关闭session的使用（前后端分离项目，session就不管用了）
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //禁用csrf防御
        http.csrf().disable();

        //允许跨域
        http.cors();
    }

    //配置放行接口
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/swagger-ui.html/**")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources/**")
                //放行报名端口
                .antMatchers("/v2/**");
    }


    //设置加密方式
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    //暴露 AuthenticationManager bean 实例
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

package com.monkey.spring_security;


import com.monkey.spring_security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 注册自定义的异常处理类
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .authorizeRequests()
                // 用户界面放行
                .antMatchers("/monkey-user/user/login/**", "/monkey-user/user/register",
                        "/monkey-user/user/getUserInfoBytoken", "/monkey-user/user/getCaptcha",
                        "/monkey-user/user/sendVerfyCode").permitAll()
                // 博客界面放行
                .antMatchers("/monkey-article/blog/getArticleContentByLabelId", "/monkey-article/blog/getArticlePagination",
                        "/monkey-article/blog/fireRecently", "/monkey-article/blog/getArticleInformationByArticleId", "/monkey-article/blog/getArticleListBySort").permitAll()
                .antMatchers("/monkey-article/blog/label/getLabelList").permitAll()
                // 发布文章界面
                .antMatchers("/monkey-article/publish/getOneLevelLabelList", "/monkey-article/publish/getTwoLabelListByOneLabelId").permitAll()
                // 查看文章界面放行
                .antMatchers("/monkey-article/check/getArticleLabelInfoByArticleId",
                        "/monkey-article/check/getAuthorInfoByArticleId", "/monkey-article/check/addAtricleVisit", "/monkey-article/check/getCommentInformationByArticleId").permitAll()
                // 用户主页界面
                .antMatchers("/monkey-user/user/center/home/**").permitAll()
                // 用户问答列表界面
                .antMatchers("/monkey-question/question/getLatestQuestionList", "/monkey-question/question/getWaitYouQuestionList",
                        "/monkey-question/question/getHottestQuestionList", "/monkey-question/question/getRightHottestQuestionList",
                        "/monkey-question/question/questionViewCountAddOne").permitAll()
                // 用户问答回复界面
                .antMatchers("/monkey-question/reply/getAuthorVoInfoByQuestionId", "/monkey-question/reply/getQuestionInfoByQuestionId",
                        "/monkey-question/reply/getQuestionLabelNameByQuestionId", "/monkey-question/reply/getQuestionReplyListByQuestionId",
                        "/monkey-question/reply/getQuestionCommentByQuestionReplyId").permitAll()
                // admin-server
                .antMatchers("/actuator/**", "/login").permitAll()
                // 课程主页列表
                .antMatchers("/monkey-course/course/**").permitAll()
                // netty模块调用用户模块
                .antMatchers("/monkey-user/feign/**").permitAll()
                .antMatchers("/test/**").permitAll()
                // 用户文章模块feign调用
                .antMatchers("/monkey-article/user/feign/**").permitAll()
                // 用户问答模块feign调用
                .antMatchers("/monkey-question/user/feign/**").permitAll()
                // 用户课程模块feign调用
                .antMatchers("/monkey-course/user/feign/**").permitAll()
                // 课程详情页面
                .antMatchers("/monkey-course/detail/**").permitAll()
                // 课程评论接口
                .antMatchers("/monkey-course/comment/getCourseCommentList").permitAll()
                .antMatchers("/monkey-course/video/**").permitAll()
                // 放行swagger
                .antMatchers("/swagger-ui.html/**", "/swagger-ui/**", "/doc.html/**", "/swagger-resources/**", "/webjars/**", "/v3/**",
                        "/v2/**",
                        "/favicon.ico").permitAll()
                // 视频点播
                .antMatchers("/monkey-service/aliyun/video/**").permitAll()
                // 课程播放界面
                .antMatchers("/monkey-course/video/player/getCourseInfoByCourseId",
                        "/monkey-course/video/player/getCourseDirectoryByCourseId",
                        "/monkey-course/video/player/getBarrageListByCourseVideoId, " +
                        "/monkey-course/video/player/getCourseScoreInfo",
                        "/monkey-course/video/player/getCourseScoreUserList",
                        "/monkey-course/video/player/getUserInfo",
                        "/monkey-course/video/player/judgeIsFans",
                        "/monkey-course/video/player/getTeacherOtherCourse",
                        "/monkey-course/video/player/getFireCourseList").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()

                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    // 方向websocket请求
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/websocket/**");
    }

}

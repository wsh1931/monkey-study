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
                .authorizeRequests()
                // 用户界面放行
                .antMatchers("/user/login/**", "/user/register", "/user/getUserInfoBytoken", "/user/getCaptcha",
                        "/user/sendVerfyCode").permitAll()
                // 博客界面放行
                .antMatchers("/blog/article/getArticleContentByLabelId", "/blog/article/getArticlePagination",
                        "/blog/article/fireRecently", "/blog/article/getArticleInformationByArticleId", "/blog/article/getArticleListBySort").permitAll()
                .antMatchers("/blog/label/getLabelList").permitAll()
                // 发布文章界面
                .antMatchers("/publish/getOneLevelLabelList", "/publish/getTwoLabelListByOneLabelId").permitAll()
                // 查看文章界面放行
                .antMatchers("/check/article/getArticleLabelInfoByArticleId",
                        "/check/article/getAuthorInfoByArticleId", "/check/article/addAtricleVisit", "/check/article/getCommentInformationByArticleId").permitAll()
                // 用户主页界面
                .antMatchers("/user/center/home/**").permitAll()
                // 用户问答列表界面
                .antMatchers("/question/getLatestQuestionList", "/question/getWaitYouQuestionList",
                        "/question/getHottestQuestionList", "/question/getRightHottestQuestionList").permitAll()
                // 用户问答回复界面
                .antMatchers("/question/reply/getAuthorVoInfoByQuestionId", "/question/reply/getQuestionInfoByQuestionId",
                        "/question/reply/getQuestionLabelNameByQuestionId", "/question/reply/getQuestionReplyListByQuestionId",
                        "/question/reply/getQuestionCommentByQuestionReplyId").permitAll()
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

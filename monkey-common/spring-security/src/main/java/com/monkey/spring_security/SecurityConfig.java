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
                .antMatchers("/monkey-article/publish/getOneLevelLabelList", "/monkey-article/publish/getTwoLabelListByOneLabelId",
                        "/monkey-article/publish/likeSearchOneLabel").permitAll()
                // 查看文章界面放行
                .antMatchers("/monkey-article/check/getArticleLabelInfoByArticleId",
                        "/monkey-article/check/getAuthorInfoByArticleId", "/monkey-article/check/addArticleVisit", "/monkey-article/check/getCommentInformationByArticleId").permitAll()
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
                .antMatchers("/monkey-course/comment/getCourseCommentList",
                        "/monkey-course/comment/judgeIsAuthor").permitAll()
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
                // 课程支付
                .antMatchers("/monkey-course/pay/finishPayNotice").permitAll()

                // 社区主页
                .antMatchers("/monkey-community/community/queryLikeArticleList", "/monkey-community/community/queryReplyArticleList",
                        "/monkey-community/community/queryViewArticleList", "/monkey-community/community/queryCollectArticleList",
                        "/monkey-community/community/queryHireArticleList", "/monkey-community/community/queryLatestArticleList",
                        "/monkey-community/community/getOneLevelLabelList", "/monkey-community/community/getTwoLabelListByOneLabelId",
                        "/monkey-community/community/queryOneLabel", "/monkey-community/community/searchArticle",
                        "/monkey-community/community/queryHireCommunityList", "/monkey-community/community/queryLatestCommunityList",
                        "/monkey-community/community/articleViewCount/addOne",
                        "/monkey-community/community/queryUserManageCommunity",
                        "/monkey-community/community/queryHireArticleList").permitAll()
                // 社区发布文章
                .antMatchers("/monkey-community/publish/queryCommunityChannelListByCommunityId").permitAll()
                // 社区详情页面
                .antMatchers("/monkey-community/community/detail/**",
                        "/monkey-community/community/detail/queryLatestArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryHottestArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryScoreArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryViewsArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryExcellentArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryTopArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryLikeArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryCollectArticleList/ByChannelId/CommunityId",
                        "/monkey-community/community/detail/queryMyAddCommunityCount",
                        "/monkey-community/community/detail/queryMyManageCommunityCount",
                        "/monkey-community/community/detail/queryRecommendCommunityCount",
                        "/monkey-community/community/detail/queryMyAddCommunityList",
                        "/monkey-community/community/detail/queryMyManegeCommunityList",
                        "/monkey-community/community/detail/queryRecommendCommunityList",
                        "/monkey-community/community/detail/queryOtherCommunityListList",
                        "/monkey-community/community/detail/searchCommunityByCommunityName",
                        "/monkey-community/community/detail/searchArticleContent").permitAll()
                // 社区详情卡片路径
                .antMatchers("/monkey-community/community/detail/card/judgePower").permitAll()
                // 社区基本信息
                .antMatchers("/monkey-community/community/baseInfo/**").permitAll()
                // 社区文章界面
                .antMatchers("/monkey-community/article/queryArticleBaseInfo",
                        "/monkey-community/article/queryCommunityArticle/score",
                        "/monkey-community/article/queryArticleVoteInfo",
                        "/monkey-community/article/queryTaskInfo/judgeIsExpire",
                        "/monkey-community/article/queryNoSubmitTaskPeople",
                        "/monkey-community/article/queryTaskSubmit/historyRecords",
                        "/monkey-community/article/exportDataToExcel",
                        "/monkey-community/article/judgeIsShowTask",
                        "/monkey-community/article/queryUserArticleScore",
                        "/monkey-community/article/judgeIsLikeArticle",
                        "/monkey-community/article/judgeIsCollectArticle",
                        "/monkey-community/article/judgeIsAuthorOrManager",
                        "/monkey-community/article/queryCommunityArticle/channelName",
                        "/monkey-community/article/queryCommunityIdByArticleId").permitAll()
                // 社区文章评论
                .antMatchers("/monkey-community/comment/queryDefault/commentList",
                        "/monkey-community/comment/query/timeUpgrade/comment",
                        "/monkey-community/comment/query/timeDownSort/comment",
                        "/monkey-community/comment//query/notReply/comment").permitAll()
                // 用户调用社区feign
                .antMatchers("/monkey-community/user/feign/**").permitAll()
                // 社区排行
                .antMatchers("/monkey-community/rank/**").permitAll()
                // 社区用户管理
                .antMatchers("/monkey-community/manage/userManage/exportData").permitAll()
                // 社区内容管理
                .antMatchers("/monkey-community/manage/contentManage/judgeCommunityArticleIsExist").permitAll()
                // 资源分类接口
                .antMatchers("/monkey-resource/classification/**").permitAll()
                // 资源主页接口
                .antMatchers("/monkey-resource/homePage/**").permitAll()
                // 资源搜索接口
                .antMatchers("/monkey-resource/search/**").permitAll()
                // 资源详情页面
                .antMatchers("/monkey-resource/detail/**").permitAll()
                // 资源评论页面
                .antMatchers("/monkey-resource/comment/queryCommentList",
                        "/monkey-resource/comment/judgeIsAuthor",
                        "/monkey-resource/comment/query/timeDownSort/comment",
                        "/monkey-resource/comment/query/timeUpgrade/comment",
                        "/monkey-resource/comment/judgeIsAuthor").permitAll()
                // 资源购买界面
                .antMatchers("/monkey-resource/pay/finishPayNotice").permitAll()
                // 用户模块调用资源模块feign接口
                .antMatchers("/monkey-resource/user/feign/**").permitAll()
                // 用户vip
                .antMatchers("/monkey-user/vip/queryVipPrivilegeList",
                        "/monkey-user/vip/queryVipPrice",
                        "/monkey-user/vip/judgeIsVip",
                        "/monkey-user/vip/finishPayNotice").permitAll()
                // 举报接口
                .antMatchers("/monkey-user/report/queryOneReportType",
                        "/monkey-user/report/queryTwoReportType").permitAll()

                // 放行搜索模块调用文章模块feign接口
                .antMatchers("/monkey-article/search/feign/**").permitAll()
                // 全局搜索文章功能接口放行
                .antMatchers("/monkey-search/article/**").permitAll()
                // 全局问答所搜功能接口放行
                .antMatchers("/monkey-search/question/**").permitAll()
                // 放行搜索模块调用问答模块feign接口
                .antMatchers("/monkey-question/search/feign/**").permitAll()
                // 问答模块调用搜索模块feign接口放行
                .antMatchers("/monkey-search/question/feign/**").permitAll()
                // 文章模块调用搜索模块feign接口放行
                .antMatchers("/monkey-search/article/feign/**").permitAll()
                // 课程模块调用搜索模块feign接口放行
                .antMatchers("/monkey-course/search/feign/**").permitAll()
                // 放行elasticsearch课程模块接口
                .antMatchers("/monkey-search/course/**").permitAll()
                // 放行elasticsearch课程模块调用搜索模块接口
                .antMatchers("/monkey-search/course/feign/**").permitAll()
                // 放行elasticsearch社区文章接口
                .antMatchers("/monkey-search/community/article/**").permitAll()
                // 放行搜索模块调用社区模块接口
                .antMatchers("/monkey-community/search/feign/**").permitAll()
                // 放行社区文章调用搜索模块接口
                .antMatchers("/monkey-community/community/article/feign").permitAll()
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

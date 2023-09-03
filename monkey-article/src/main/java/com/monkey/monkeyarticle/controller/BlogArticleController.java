package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.service.BlogArticleService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/monkey-article/blog")
public class BlogArticleController {

    @Autowired
    private BlogArticleService blogArticleService;

//    @Value("${hero.name}")
//    private String heroname;
    // 通过标签id得到文章内容
    @GetMapping("/getArticleContentByLabelId")
    private ResultVO getArticleContentByLabelId(@RequestParam Map<String, String> data) {
        String labelId = data.get("labelId");
        return blogArticleService.getArticleContentByLabelId(labelId);
    }

    // 博客主页得到所有文章以及分页功能实现
    @GetMapping("/getArticlePagination")
    private ResultVO getArticlePagination(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long labelId = Long.parseLong(data.get("labelId"));
        String userId = JwtUtil.getUserId();
        return blogArticleService.getArticlePagination(currentPage, pageSize, labelId, userId);
    }

    // 查询最近热帖
    @GetMapping("/fireRecently")
    private ResultVO getRecentlyFireArticle() {
        return blogArticleService.getRecentlyFireArticle();
    }

    // 用户点赞文章
    @GetMapping("/userClickPraise")
    private ResultVO userClickPraise(@RequestParam Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        long userId = Long.parseLong(data.get("userId"));
        return blogArticleService.userClickPraise(articleId, userId);
    }

    // 用户取消点赞
    @GetMapping("/userClickOppose")
    private ResultVO userClickOppose(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        return blogArticleService.userClickOppose(articleId, userId);
    }

    // 通过文章id得到文章信息
    @GetMapping("/getArticleInformationByArticleId")
    private ResultVO getArticleInformationByArticleId(@RequestParam Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        String userId = JwtUtil.getUserId();
        return blogArticleService.getArticleInformationByArticleId(articleId, userId);
    }

    // 按排序字段得到文章列表
    @GetMapping("/getArticleListBySort")
    public ResultVO getArticleListBySort() {
        return blogArticleService.getArticleListBySort();
    }
}

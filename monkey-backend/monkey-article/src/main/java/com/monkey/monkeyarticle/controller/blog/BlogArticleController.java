package com.monkey.monkeyarticle.controller.blog;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.blog.BlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/blog/article")
public class BlogArticleController {

    @Autowired
    private BlogArticleService blogArticleService;
    // 通过标签id得到文章内容
    @GetMapping("/getArticleContentByLabelId")
    private ResultVO getArticleContentByLabelId(@RequestParam Map<String, String> data) {
        String labelId = data.get("labelId");
        return blogArticleService.getArticleContentByLabelId(labelId);
    }

    // 博客主页得到所有文章以及分页功能实现
    @GetMapping("/getArticlePagination")
    private ResultVO getArticlePagination(@RequestParam Map<String, String> data) {
        return blogArticleService.getArticlePagination(data);
    }

    // 查询最近热帖
    @GetMapping("/fireRecently")
    private ResultVO getRecentlyFireArticle() {
        return blogArticleService.getRecentlyFireArticle();
    }

    // 用户点赞文章
    @GetMapping("/userClickPraise")
    private ResultVO userClickPraise(@RequestParam Map<String, String> data) {
        return blogArticleService.userClickPraise(data);
    }

    // 用户取消点赞
    @GetMapping("/userClickOppose")
    private ResultVO userClickOppose(@RequestParam Map<String, String> data) {
        return blogArticleService.userClickOppose(data);
    }

    // 用户收藏文章
    @GetMapping("/userCollect")
    private ResultVO userCollect(@RequestParam Map<String, String> data) {
        return blogArticleService.userCollect(data);
    }

    // 通过文章id得到文章信息
    @GetMapping("/getArticleInformationByArticleId")
    private ResultVO getArticleInformationByArticleId(@RequestParam Map<String, String> data) {
        return blogArticleService.getArticleInformationByArticleId(data);
    }
}

package com.monkey.monkeybackend.Controller.User.blog;

import com.monkey.monkeybackend.Service.Blog.ArticleService;
import com.monkey.monkeybackend.utils.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/blog/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    // 通过标签id得到文章内容
    @GetMapping("/getArticleContentByLabelId")
    private ResultVO getArticleContentByLabelId(@RequestParam Map<String, String> data) {
        String labelId = data.get("labelId");
        return articleService.getArticleContentByLabelId(labelId);
    }

    // 博客主页分页实现
    @GetMapping("/pagination")
    private ResultVO pagination(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long labelId = Long.parseLong(data.get("labelId"));
        return articleService.pagination(currentPage, pageSize, labelId);
    }

    // 查询最近热帖
    @GetMapping("/fireRecently")
    private ResultVO getRecentlyFireArticle() {
        return articleService.getRecentlyFireArticle();
    }

    // 用户点赞文章
    @GetMapping("/userClickPraise")
    private ResultVO userClickPraise(@RequestParam Map<String, String> data) {
        return articleService.userClickPraise(data);
    }

    // 用户取消点赞
    @GetMapping("/userClickOppose")
    private ResultVO userClickOppose(@RequestParam Map<String, String> data) {
        return articleService.userClickOppose(data);
    }

    @GetMapping("/userCollect")
    private ResultVO userCollect(@RequestParam Map<String, String> data) {
        return articleService.userCollect(data);
    }

    // 通过文章id得到文章信息
    @GetMapping("/getArticleInformationByArticleId")
    private ResultVO getArticleInformationByArticleId(@RequestParam Map<String, String> data) {
        return articleService.getArticleInformationByArticleId(data);
    }
}

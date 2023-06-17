package com.monkey.monkeyarticle.controller.check;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.check.CheckArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/check/article")
public class CheckArticleController {
    @Autowired
    private CheckArticleService checkArticleService;

    // 通过文章id查询文章标签信息
    @GetMapping("/getArticleLabelInfoByArticleId")
    public ResultVO getArticleLabelInfoByArticleId(@RequestParam Map<String, String> data) {
        return checkArticleService.getArticleLabelInfoByArticleId(data);
    }

    // 通过文章id得到作者信息
    @GetMapping("/getAuthorInfoByArticleId")
    public ResultVO getAuthorInfoByArticleId(@RequestParam Map<String, String> data) {
        return checkArticleService.getAuthorInfoByArticleId(data);
    }

    // 游览该文章，文章游览数加一
    @PostMapping("/addAtricleVisit")
    public ResultVO addAtricleVisit(@RequestParam Map<String, String> data) {
        return checkArticleService.addArticleVisit(data);
    }

    // 关注作者
    @GetMapping("/likeAuthor")
    public ResultVO likeAuthor(@RequestParam Map<String, String> data) {
        return checkArticleService.likeAuthor(data);
    }

    // 通过文章id查询该文章评论信息
    @GetMapping("/getCommentInformationByArticleId")
    public ResultVO getCommentInformationByArticleId(@RequestParam Map<String, String> data) {
        return checkArticleService.getCommentInformationByArticleId(data);
    }

    // 发布评论
    @GetMapping("/publishComment")
    public ResultVO publishComment(@RequestParam Map<String, String> data) {
        return checkArticleService.publishComment(data);
    }

    // 评论点赞功能实现
    @PostMapping("/commentLike")
    public ResultVO commentLike(@RequestParam Map<String, String> data) {
        return checkArticleService.commentLike(data);
    }

    // 回复评论功能
    @PostMapping("/replyComment")
    public ResultVO replyComment(@RequestParam Map<String, String> data) {
        return checkArticleService.replyComment(data);
    }
}

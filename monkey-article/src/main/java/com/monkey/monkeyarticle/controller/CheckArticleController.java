package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.CheckArticleService;
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
        long articleId = Long.parseLong(data.get("articleId"));
        return checkArticleService.getArticleLabelInfoByArticleId(articleId);
    }

    // 通过文章id得到作者信息
    @GetMapping("/getAuthorInfoByArticleId")
    public ResultVO getAuthorInfoByArticleId(@RequestParam Map<String, String> data) {
        String fansId = data.get("userId");
        long articleId = Long.parseLong(data.get("articleId"));
        return checkArticleService.getAuthorInfoByArticleId(articleId, fansId);
    }

    // 游览该文章，文章游览数加一
    @PostMapping("/addAtricleVisit")
    public ResultVO addAtricleVisit(@RequestParam Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        return checkArticleService.addArticleVisit(articleId);
    }

    // 关注作者
    @GetMapping("/likeAuthor")
    public ResultVO likeAuthor(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId")); // 被关注者id
        return checkArticleService.likeAuthor(userId);
    }

    // 通过文章id查询该文章评论信息
    @GetMapping("/getCommentInformationByArticleId")
    public ResultVO getCommentInformationByArticleId(@RequestParam Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        String isLikeUserId = data.get("userId");
        return checkArticleService.getCommentInformationByArticleId(articleId, isLikeUserId);
    }

    // 发布评论
    @GetMapping("/publishComment")
    public ResultVO publishComment(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        String content = data.get("content");
        return checkArticleService.publishComment(userId,articleId, content);
    }

    // 评论点赞功能实现
    @PostMapping("/commentLike")
    public ResultVO commentLike(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        long commentId = Long.parseLong(data.get("commentId"));
        return checkArticleService.commentLike(userId, articleId, commentId);
    }

    // 回复评论功能
    @PostMapping("/replyComment")
    public ResultVO replyComment(@RequestParam Map<String, String> data) {
        long commentId = Long.parseLong(data.get("commentId"));
        long replyId = Long.parseLong(data.get("replyId"));
        String replyContent = data.get("replyContent");
        return checkArticleService.replyComment(commentId, replyId, replyContent);
    }
}

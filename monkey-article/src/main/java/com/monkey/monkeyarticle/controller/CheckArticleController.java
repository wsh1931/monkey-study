package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.CheckArticleService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "查看文章页面接口")
@RestController
@RequestMapping("/monkey-article/check")
public class CheckArticleController {
    @Resource
    private CheckArticleService checkArticleService;

    @ApiOperation("通过文章id查询文章标签信息")
    @GetMapping("/getArticleLabelInfoByArticleId")
    public ResultVO getArticleLabelInfoByArticleId(@RequestParam("articleId") @ApiParam("文章id")Long articleId) {
        return checkArticleService.getArticleLabelInfoByArticleId(articleId);
    }

    @ApiOperation("通过文章id得到作者信息")
    @GetMapping("/getAuthorInfoByArticleId")
    public ResultVO getAuthorInfoByArticleId(@RequestParam("articleId") @ApiParam("文章id")Long articleId) {
        String fansId = JwtUtil.getUserId();
        return checkArticleService.getAuthorInfoByArticleId(articleId, fansId);
    }

    @ApiOperation("游览该文章，文章游览数加一")
    @PostMapping("/addAtricleVisit")
    public ResultVO addAtricleVisit(@RequestParam("articleId") @ApiParam("文章id")Long articleId) {
        return checkArticleService.addArticleVisit(articleId);
    }

    @GetMapping("/likeAuthor")
    @ApiOperation("关注作者")
    public ResultVO likeAuthor(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId")); // 被关注者id
        return checkArticleService.likeAuthor(userId);
    }

    @ApiOperation("通过文章id查询该文章评论信息")
    @GetMapping("/getCommentInformationByArticleId")
    public ResultVO getCommentInformationByArticleId(@RequestParam("articleId") @ApiParam("文章id")Long articleId,
                                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String isLikeUserId = JwtUtil.getUserId();
        return checkArticleService.getCommentInformationByArticleId(articleId, isLikeUserId, currentPage, pageSize);
    }

    @GetMapping("/publishComment")
    @ApiOperation("发布评论")
    public ResultVO publishComment(@RequestParam("articleId") @ApiParam("文章id")Long articleId,
                                   @RequestParam("content") @ApiParam("评论内容")String content) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return checkArticleService.publishComment(userId,articleId, content);
    }

    @PostMapping("/commentLike")
    @ApiOperation("评论点赞功能实现")
    public ResultVO commentLike(@RequestParam("articleId") @ApiParam("文章id")Long articleId,
                                @RequestParam("commentId") @ApiParam("评论id")Long commentId,
                                @RequestParam("recipientId") @ApiParam("消息接收者id")Long recipientId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return checkArticleService.commentLike(userId, articleId, commentId, recipientId);
    }

    @PostMapping("/replyComment")
    @ApiOperation("回复评论功能")
    public ResultVO replyComment(@RequestParam("replyContent") @ApiParam("回复内容")String replyContent,
                                 @RequestParam("commentId") @ApiParam("评论id")Long commentId) {
        long replyId = Long.parseLong(JwtUtil.getUserId());
        return checkArticleService.replyComment(commentId, replyId, replyContent);
    }
}

package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityCommentService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/22 16:17
 * @version: 1.0
 * @description: 社区评论controller
 */
@Api(tags = "社区文章评论接口")
@RestController
@RequestMapping("/monkey-community/comment")
public class CommunityCommentController {
    @Resource
    private CommunityCommentService communityCommentService;

    @ApiOperation("查询默认排序评论列表")
    @GetMapping("/queryDefault/commentList")
    public R queryDefaultCommentList(@RequestParam("communityArticleId") @ApiParam("文章评论id") Long communityArticleId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return communityCommentService.queryDefaultCommentList(userId, communityArticleId, currentPage, pageSize);
    }

    @ApiOperation("查询时间升序评论列表")
    @GetMapping("/query/timeUpgrade/comment")
    public R queryTimeUpgradeComment(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return communityCommentService.queryTimeUpgradeComment(userId, communityArticleId, currentPage, pageSize);
    }

    @ApiOperation("得到时间降序评论列表")
    @GetMapping("/query/timeDownSort/comment")
    public R queryTimeDownSortComment(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                      @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return communityCommentService.queryTimeDownSortComment(userId, communityArticleId, currentPage, pageSize);
    }

    @ApiOperation("查询未回复评论集合")
    @GetMapping("/query/notReply/comment")
    public R queryNotReplyCommentList(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                      @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return communityCommentService.queryNotReplyCommentList(userId, communityArticleId, currentPage, pageSize);
    }

    @ApiOperation("精选评论")
    @PutMapping("/curationComment")
    public R curationComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return communityCommentService.curationComment(commentId);
    }

    @ApiOperation("取消精选评论")
    @PutMapping("/cancelCurationComment")
    public R cancelCurationComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return communityCommentService.cancelCurationComment(commentId);
    }

    @ApiOperation("置顶评论")
    @PutMapping("/topComment")
    public R topComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return communityCommentService.topComment(commentId);
    }

    @ApiOperation("取消置顶评论")
    @PutMapping("/cancelTopComment")
    public R cancelTopComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return communityCommentService.cancelTopComment(commentId);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/deleteComment")
    public R deleteComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId,
                           @RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityCommentService.deleteComment(commentId, communityArticleId);
    }

    @ApiOperation("发表社区文章评论")
    @PostMapping("/publishComment")
    public R publishComment(@RequestParam("commentContent") @ApiParam("评论内容") String commentContent,
                            @RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityCommentService.publishComment(userId, communityArticleId, commentContent);
    }

    @ApiOperation("发表评论回复")
    @PostMapping("/publishCommentReply")
    public R publishCommentReply(@RequestParam("senderId") @ApiParam("回复者id") Long senderId,
                                 @RequestParam("parentId") @ApiParam("评论父id") Long parentId,
                                 @RequestParam("replyContent") @ApiParam("回复内容") String replyContent,
                                 @RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        long replyId = Long.parseLong(JwtUtil.getUserId());
        return communityCommentService.publishCommentReply(senderId, parentId, replyId, replyContent, communityArticleId);
    }

    @ApiOperation("评论点赞")
    @PostMapping("/commentLike")
    public R commentLike(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityCommentService.commentLike(userId, commentId);
    }

    @ApiOperation("取消评论点赞")
    @PostMapping("/cancelCommentLike")
    public R cancelCommentLike(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityCommentService.cancelCommentLike(userId, commentId);
    }
}

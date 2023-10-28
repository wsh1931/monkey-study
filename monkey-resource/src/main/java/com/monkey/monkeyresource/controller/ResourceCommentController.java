package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourceCommentService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/18 16:39
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源评论接口")
@RestController
@RequestMapping("/monkey-resource/comment")
public class ResourceCommentController {
    @Resource
    private ResourceCommentService resourceCommentService;

    @ApiOperation("发表评论方法")
    @PostMapping("/publishCommentMethod")
    public R publishCommentMethod(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                  @RequestParam("commentContent") @ApiParam("评论内容") String commentContent) {
         long userId = Long.parseLong(JwtUtil.getUserId());
         return resourceCommentService.publishCommentMethod(resourceId, userId, commentContent);
     }

    @ApiOperation("查询评论列表")
    @GetMapping("/queryCommentList")
    public R queryCommentList(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                              @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
         String userId = JwtUtil.getUserId();
         return resourceCommentService.queryCommentList(resourceId, userId, currentPage, pageSize);
     }

    @ApiOperation("判断当前登录用户是否是文章作者")
    @GetMapping("/judgeIsAuthor")
    public R judgeIsAuthor(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        String userId = JwtUtil.getUserId();
        return resourceCommentService.judgeIsAuthor(resourceId, userId);
    }

    @ApiOperation("得到时间降序评论列表")
    @GetMapping("/query/timeDownSort/comment")
    public R queryTimeDownSortComment(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                      @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        return resourceCommentService.queryTimeDownSortComment(userId, resourceId, currentPage, pageSize);
    }

    @ApiOperation("得到时间升序评论列表")
    @GetMapping("/query/timeUpgrade/comment")
    public R queryTimeUpgradeComment(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                      @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        return resourceCommentService.queryTimeUpgradeComment(userId, resourceId, currentPage, pageSize);
    }

    @ApiOperation("查询未回复评论列表")
    @GetMapping("/queryNotReplyComment")
    public R queryNotReplyComment(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        return resourceCommentService.queryNotReplyComment(userId, resourceId, currentPage, pageSize);
    }

    @ApiOperation("发表评论回复")
    @PostMapping("/publishCommentReply")
    public R publishCommentReply(@RequestParam("senderId") @ApiParam("回复者id") Long senderId,
                                 @RequestParam("parentId") @ApiParam("评论父id") Long parentId,
                                 @RequestParam("replyContent") @ApiParam("回复内容") String replyContent,
                                 @RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long replyId = Long.parseLong(JwtUtil.getUserId());
        return resourceCommentService.publishCommentReply(senderId, parentId, replyId, replyContent, resourceId);
    }

    @ApiOperation("评论点赞")
    @PostMapping("/commentLike")
    public R commentLike(@RequestParam("commentId") @ApiParam("评论id") Long commentId,
                         @RequestParam("recipientId") @ApiParam("接收者id") Long recipientId,
                         @RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceCommentService.commentLike(userId, commentId, recipientId, resourceId);
    }

    @ApiOperation("取消评论点赞")
    @PostMapping("/cancelCommentLike")
    public R cancelCommentLike(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceCommentService.cancelCommentLike(userId, commentId);
    }

    @ApiOperation("精选评论")
    @PutMapping("/curationComment")
    public R curationComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return resourceCommentService.curationComment(commentId);
    }

    @ApiOperation("取消精选评论")
    @PutMapping("/cancelCurationComment")
    public R cancelCurationComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return resourceCommentService.cancelCurationComment(commentId);
    }

    @ApiOperation("置顶评论")
    @PutMapping("/topComment")
    public R topComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return resourceCommentService.topComment(commentId);
    }

    @ApiOperation("取消置顶评论")
    @PutMapping("/cancelTopComment")
    public R cancelTopComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return resourceCommentService.cancelTopComment(commentId);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/deleteComment")
    public R deleteComment(@RequestParam("commentId") @ApiParam("评论id") Long commentId,
                           @RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceCommentService.deleteComment(commentId, resourceId);
    }
}

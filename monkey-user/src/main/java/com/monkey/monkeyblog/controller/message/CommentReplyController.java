package com.monkey.monkeyblog.controller.message;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.CommentReplyService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/25 17:12
 * @version: 1.0
 * @description:
 */
@Api(tags = "评论回复")
@RestController
@RequestMapping("/monkey-user/message/comment/reply")
public class CommentReplyController {
    @Resource
    private CommentReplyService commentReplyService;

    @ApiOperation("查询评论回复消息")
    @GetMapping("/queryCommentReplyMessage")
    public R queryCommentReplyMessage(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        return commentReplyService.queryCommentReplyMessage(Long.parseLong(userId), currentPage, pageSize);
    }

    @ApiOperation("删除评论回复消息")
    @DeleteMapping("/deleteCommentReply")
    public R deleteCommentReply(@RequestParam("commentReplyId") @ApiParam("评论回复id") Long commentReplyId) {
        return commentReplyService.deleteCommentReply(commentReplyId);
    }

    @ApiOperation("将所有评论回复置为已读")
    @PutMapping("/updateAllCommentReplyAlready")
    public R updateAllCommentReplyAlready() {
        String userId = JwtUtil.getUserId();
        return commentReplyService.updateAllCommentReplyAlready(userId);
    }

    @ApiOperation("删除所有评论回复已读消息")
    @DeleteMapping("/deleteAllCommentMessageOfAlreadyRead")
    public R deleteAllCommentMessageOfAlreadyRead() {
        String userId = JwtUtil.getUserId();
        return commentReplyService.deleteAllCommentMessageOfAlreadyRead(userId);
    }
}

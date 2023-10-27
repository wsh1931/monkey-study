package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionReplyCommentService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.util.Map;
import java.util.Scanner;


@Api(tags = "发布问答接口")
@RestController
@RequestMapping("/monkey-question/reply/comment")
public class QuestionReplyCommentController {
    @Resource
    private QuestionReplyCommentService questionReplyCommentService;

    @ApiOperation("发布问答评论")
    @PostMapping("/publishQuestionComment")
    public ResultVO publishQuestionComment(@RequestParam("questionReplyId") @ApiParam("问答回复id") Long questionReplyId,
                                           @RequestParam("commentContent") @ApiParam("评论内容") String commentContent,
                                           @RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return questionReplyCommentService.publishQuestionComment(userId, questionReplyId, commentContent, questionId);
    }

    @ApiOperation("问答评论回复功能实现")
    @PostMapping("/questionReplyComment")
    public ResultVO questionReplyComment(@RequestParam("parentId") @ApiParam("评论父id") Long parentId,
                                         @RequestParam("questionReplyContent") @ApiParam("回复评论内容") String questionReplyContent,
                                         @RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                         @RequestParam("recipientId") @ApiParam("接收者id") Long recipientId) {
        long replyId = Long.parseLong(JwtUtil.getUserId());
        return questionReplyCommentService.questionReplyComment(parentId, replyId, questionReplyContent, questionId, recipientId);
    }
}

package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Scanner;


@RestController
@RequestMapping("/monkey-question/reply/comment")
public class QuestionReplyCommentController {
    @Autowired
    private QuestionReplyCommentService questionReplyCommentService;

    // 发布问答评论
    @PostMapping("/publishQuestionComment")
    public ResultVO publishQuestionComment(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long questionReplyId = Long.parseLong(data.get("questionReplyId"));
        String commentContent = data.get("commentContent");
        return questionReplyCommentService.publishQuestionComment(userId, questionReplyId, commentContent);
    }

    // 问答评论回复功能实现
    @PostMapping("/questionReplyComment")
    public ResultVO questionReplyComment(@RequestParam Map<String, String> data) {
        long parentId = Long.parseLong(data.get("parentId"));
        long replyId = Long.parseLong(data.get("replyId"));
        String questionReplyContent = data.get("questionReplyContent");
        return questionReplyCommentService.questionReplyComment(parentId, replyId, questionReplyContent);
    }
}

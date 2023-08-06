package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionReplyService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/monkey-question/reply")
public class QusetionReplyController{

    @Autowired
    private QuestionReplyService questionReplyService;

    // 通过问答id得到作者Vo信息
    @GetMapping("/getAuthorVoInfoByQuestionId")
    public ResultVO getAuthorVoInfoByQuestionId(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        String fansId = JwtUtil.getUserId();
        return questionReplyService.getAuthorVoInfoByQuestionId(questionId, fansId);
    }

    // 通过问答id得到问答信息
    @GetMapping("/getQuestionInfoByQuestionId")
    public ResultVO getQuestionInfoByQuestionId(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        String userId = JwtUtil.getUserId();
        return questionReplyService.getQuestionInfoByQuestionId(questionId, userId);
    }

    // 通过问答id得到问答标签名
    @GetMapping("/getQuestionLabelNameByQuestionId")
    public ResultVO getQuestionLabelNameByQuestionId(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        return questionReplyService.getQuestionLabelNameByQuestionId(questionId);
    }

    // 通过问答id得到问答回复列表
    @GetMapping("/getQuestionReplyListByQuestionId")
    public ResultVO getQuestionReplyListByQuestionId(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        String fansId = JwtUtil.getUserId();
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return questionReplyService.getQuestionReplyListByQuestionId(questionId, fansId, currentPage, pageSize);
    }


    // 用户问答点赞实现
    @PostMapping("/userLikeQuestion")
    public ResultVO userLikeQuestion(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        long userId = Long.parseLong(data.get("userId"));
        return questionReplyService.userLikeQuestion(questionId, userId);
    }

    // 用户问答取消点赞实现
    @PostMapping("/userCancelLikeQuestion")
    public ResultVO userCancelLikeQuestion(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        long userId = Long.parseLong(data.get("userId"));
        return questionReplyService.userCancelLikeQuestion(questionId, userId);
    }


    // 通过问答回复id得到文章评论信息
    @GetMapping("/getQuestionCommentByQuestionReplyId")
    public ResultVO getQuestionCommentByQuestionReplyId(@RequestParam Map<String, String> data) {
        long questionReplyId = Long.parseLong(data.get("questionReplyId"));
        return questionReplyService.getQuestionCommentByQuestionReplyId(questionReplyId);
    }

    // 发表问答回复
    @PostMapping("/publishReply")
    public R publishReply(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        String replyContent = data.get("replyContent");
        return questionReplyService.publishReply(questionId, userId, replyContent);
    }
}

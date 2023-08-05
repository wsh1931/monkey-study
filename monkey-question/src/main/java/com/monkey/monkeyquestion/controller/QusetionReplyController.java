package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionReplyService;
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
        String fansId = data.get("fansId");
        return questionReplyService.getAuthorVoInfoByQuestionId(questionId, fansId);
    }

    // 通过问答id得到问答信息
    @GetMapping("/getQuestionInfoByQuestionId")
    public ResultVO getQuestionInfoByQuestionId(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        String userId = data.get("userId");
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
        String fansId = data.get("fansId");
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return questionReplyService.getQuestionReplyListByQuestionId(questionId, fansId, currentPage, pageSize);
    }

//    // 当前登录用户收藏问答
//    @PostMapping("/collectQuestion")
//    public ResultVO collectQuestion(@RequestParam Map<String, String> data) {
//        long userId = Long.parseLong(data.get("userId"));
//        long questionId = Long.parseLong(data.get("questionId"));
//        return questionReplyService.collectQuestion(userId, questionId);
//    }

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

//    // 用户收藏问答实现
//    @PostMapping("/userCollectQuestion")
//    public ResultVO userCollectQuestion(@RequestParam Map<String, String> data) {
//        long questionId = Long.parseLong(data.get("questionId"));
//        long userId = Long.parseLong(data.get("userId"));
//        return questionReplyService.userCollectQuestion(questionId, userId);
//    }

    // 通过问答回复id得到文章评论信息
    @GetMapping("/getQuestionCommentByQuestionReplyId")
    public ResultVO getQuestionCommentByQuestionReplyId(@RequestParam Map<String, String> data) {
        long questionReplyId = Long.parseLong(data.get("questionReplyId"));
        return questionReplyService.getQuestionCommentByQuestionReplyId(questionReplyId);
    }
}

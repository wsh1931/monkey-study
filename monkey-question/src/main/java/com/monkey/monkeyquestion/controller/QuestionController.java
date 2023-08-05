package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/monkey-question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 得到最新问答列表
    @GetMapping("/getLatestQuestionList")
    public ResultVO getQuestionList(@RequestParam Map<String, String> data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return questionService.getLastQuestionList(currentPage, pageSize);
    }

    // 发布问答
    @PostMapping("/publishQuestion")
    public ResultVO publishQuestion(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        String questionForm = data.get("questionForm");
        return questionService.publishQuestion(userId, questionForm);
    }

    // 得到最热问答列表
    @GetMapping("/getHottestQuestionList")
    public ResultVO getHottestQuestionList(@RequestParam Map<String, String> data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return questionService.getHottestQuestionList(currentPage, pageSize);
    }

    // 完成等你来答后端查询
    @GetMapping("/getWaitYouQuestionList")
    public ResultVO getWaitYouQuestionList(@RequestParam Map<String, String> data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        String userId = data.get("userId");
        return questionService.getWaitYouQuestionList(currentPage, pageSize, userId);
    }

    // 得到右侧热门回答列表
    @GetMapping("/getRightHottestQuestionList")
    public ResultVO getRightHottestQuestionList() {
        return questionService.getRightHottestQuestionList();
    }

    // 用过标签名模糊查询标签列表
    @GetMapping("/getLabelListByLabelName")
    public ResultVO getLabelListByLabelName(@RequestParam Map<String, String> data) {
        String labelName = data.get("labelName");
        return questionService.getLabelListByLabelName(labelName);
    }

    // 问答游览数 + 1
    @GetMapping("/questionViewCountAddOne")
    public R questionViewCountAddOne(@RequestParam Map<String, String> data) {
        long questionId = Long.parseLong(data.get("questionId"));
        return questionService.questionViewCountAddOne(questionId);
    }
}

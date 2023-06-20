package com.monkey.monkeyquestion.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 得到最新问答列表
    @GetMapping("/getLatestQuestionList")
    public ResultVO getQuestionList(@RequestParam Map<String, String> data) {
        return questionService.getLastQuestionList(data);
    }

    // 发布问答
    @PostMapping("/publishQuestion")
    public ResultVO publishQuestion(@RequestParam Map<String, String> data) {
        return questionService.publishQuestion(data);
    }

    // 得到最热问答列表
    @GetMapping("/getHottestQuestionList")
    public ResultVO getHottestQuestionList(@RequestParam Map<String, String> data) {
        return questionService.getHottestQuestionList(data);
    }

    // 完成等你来答后端查询
    @GetMapping("/getWaitYouQuestionList")
    public ResultVO getWaitYouQuestionList(@RequestParam Map<String, String> data) {
        return questionService.getWaitYouQuestionList(data);
    }

    // 得到右侧热门回答列表
    @GetMapping("/getRightHottestQuestionList")
    public ResultVO getRightHottestQuestionList() {
        return questionService.getRightHottestQuestionList();
    }

    // 用过标签名模糊查询标签列表
    @GetMapping("/getLabelListByLabelName")
    public ResultVO getLabelListByLabelName(@RequestParam Map<String, String> data) {
        return questionService.getLabelListByLabelName(data);
    }
}

package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.service.QuestionFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wusihao
 * @date: 2023/7/31 10:06
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-question/user/feign")
public class QuestionFeignController {
    @Autowired
    private QuestionFeignService questionFeignService;

    // 通过用户id得到问答列表
    @GetMapping("/getQuestionListByQuestionId/{userId}")
    public R getQuestionListByQuestionId(@PathVariable Long userId) {
        return questionFeignService.getQuestionListByQuestionId(userId);
    }

    // 通过用户id得到用户提问数
    @GetMapping("/getUserQuestionCountByUserId/{userId}")
    public R getUserQuestionCountByUserId(@PathVariable Long userId) {
        return questionFeignService.getUserQuestionCountByUserId(userId);
    }

    // 通过用户id得到文章分页提问列表
    @GetMapping("/getQuestionListByUserId/")
    public R getQuestionListByUserId(@RequestParam("userId") Long userId,
                                     @RequestParam("currentPage") Long currentPage,
                                     @RequestParam("pageSize") Long pageSize) {
        return questionFeignService.getQuestionListByUserId(userId, currentPage, pageSize);
    }

    // 问答游览数 + 1
    @PutMapping("/addQurstionViewSum/{questionId}")
    public R addQurstionViewSum(@PathVariable Long questionId) {
        return questionFeignService.addQurstionViewSum(questionId);
    }

    // 问答游览数 - 1
    @PutMapping("/subQurstionViewSum/{questionId}")
    public R subQurstionViewSum(@PathVariable Long questionId) {
        return questionFeignService.subQurstionViewSum(questionId);
    }
}

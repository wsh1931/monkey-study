package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.service.QuestionFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/10 10:08
 * @version: 1.0
 * @description:
 */
@Api(tags = "问答模块调用搜索模块接口")
@RestController
@RequestMapping("/monkey-search/question/feign")
public class QuestionFeignController {
    @Resource
    private QuestionFeignService questionFeignService;

    @ApiOperation("问答游览数 + 1")
    @PutMapping("/questionViewAddOne")
    public R questionViewAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionViewAddOne(questionId);
    }

    @ApiOperation("问答回复数 + 1")
    @PutMapping("/questionReplyCountAdd")
    public R questionReplyCountAdd(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionReplyCountAdd(questionId);
    }

    @ApiOperation("问答点赞数 + 1")
    @PutMapping("/questionLikeCountAddOne")
    public R questionLikeCountAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionLikeCountAddOne(questionId);
    }

    @ApiOperation("问答点赞数 - 1")
    @PutMapping("/questionLikeCountSubOne")
    public R questionLikeCountSubOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionLikeCountSubOne(questionId);
    }
    @ApiOperation("问答收藏数 + 1")
    @PutMapping("/questionCollectCountAddOne")
    public R questionCollectCountAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionCollectCountAddOne(questionId);
    }
    @ApiOperation("问答收藏数 - 1")
    @PutMapping("/questionCollectCountSubOne")
    public R questionCollectCountSubOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.questionCollectCountSubOne(questionId);
    }
}

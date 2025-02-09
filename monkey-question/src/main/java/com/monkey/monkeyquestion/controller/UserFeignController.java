package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.service.QuestionFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/7/31 10:06
 * @version: 1.0
 * @description:
 */
@Api(tags = "问答调用用户模块接口")
@RestController
@RequestMapping("/monkey-question/user/feign")
public class UserFeignController {
    @Resource
    private QuestionFeignService questionFeignService;

    @ApiOperation("得到问答一周发表数")
    @GetMapping("/queryQuestionCountRecentlyWeek")
    public R queryQuestionCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return questionFeignService.queryQuestionCountRecentlyWeek(userId);
    }

    @ApiOperation("通过用户id得到问答列表")
    @GetMapping("/getQuestionListByQuestionId/{userId}")
    public R getQuestionListByQuestionId(@PathVariable Long userId) {
        return questionFeignService.getQuestionListByQuestionId(userId);
    }

    @ApiOperation("通过用户id得到用户提问数")
    @GetMapping("/getUserQuestionCountByUserId/{userId}")
    public R getUserQuestionCountByUserId(@PathVariable Long userId) {
        return questionFeignService.getUserQuestionCountByUserId(userId);
    }

    @ApiOperation("通过用户id得到文章分页提问列表")
    @GetMapping("/getQuestionListByUserId/")
    public R getQuestionListByUserId(@RequestParam("userId") Long userId,
                                     @RequestParam("currentPage") Long currentPage,
                                     @RequestParam("pageSize") Long pageSize) {
        return questionFeignService.getQuestionListByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("问答收藏数 + 1")
    @PutMapping("/addQuestionCollectSum/{questionId}")
    public R addQuestionCollectSum(@PathVariable Long questionId) {
        return questionFeignService.addQuestionVCollectSum(questionId);
    }

    @ApiOperation("问答收藏数 - 1")
    @PutMapping("/subQuestionCollectSum/{questionId}")
    public R subQuestionCollectSum(@PathVariable Long questionId,
                                   @RequestParam("createTime") Date createTime) {
        return questionFeignService.subQuestionVCollectSum(questionId, createTime);
    }

    @ApiOperation("通过问答id得到问答信息")
    @GetMapping("/queryQuestionById/{questionId}")
    public R queryQuestionById(@PathVariable Long questionId) {
        return questionFeignService.queryQuestionById(questionId);
    }

    @ApiOperation("通过问答id, 评论id得到问答信息")
    @GetMapping("/queryQuestionAndCommentById")
    public R queryQuestionAndCommentById(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                               @RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return questionFeignService.queryQuestionAndCommentById(questionId, commentId);
    }

    @ApiOperation("通过问答id得到问答作者id")
    @GetMapping("/queryQuestionAuthorById")
    public Long queryQuestionAuthorById(@RequestParam("associationId") @ApiParam("问答id") Long questionId) {
        return questionFeignService.queryQuestionAuthorById(questionId);
    }

    @ApiOperation("查询问答和回复信息通过id")
    @GetMapping("/queryQuestionAndReplyById")
    public R queryQuestionAndReplyById(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                       @RequestParam("questionReplyId") @ApiParam("问答回复id") Long questionReplyId) {
        return questionFeignService.queryQuestionAndReplyById(questionId, questionReplyId);
    }
}

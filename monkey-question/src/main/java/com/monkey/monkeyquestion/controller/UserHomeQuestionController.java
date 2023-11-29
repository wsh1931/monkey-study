package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.service.UserHomeQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 16:18
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页调用问答模块接口")
@RestController
@RequestMapping("/monkey-question/user/home")
public class UserHomeQuestionController {
    @Resource
    private UserHomeQuestionService userHomeQuestionService;

    @ApiOperation("通过用户id查询用户发布问答")
    @GetMapping("/queryPublishQuestion")
    public R queryPublishQuestion(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeQuestionService.queryPublishQuestion(userId, currentPage, pageSize);
    }

    @ApiOperation("通过用户id查询用户回复问答")
    @GetMapping("/queryReplyQuestion")
    public R queryReplyQuestion(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeQuestionService.queryReplyQuestion(userId, currentPage, pageSize);
    }

    @ApiOperation("删除问答")
    @DeleteMapping("/deleteQuestion")
    @PreAuthorize("@questionCustomAuthority.judgeIsAuthor(#questionId)")
    public R deleteQuestion(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return userHomeQuestionService.deleteQuestion(questionId);
    }
}

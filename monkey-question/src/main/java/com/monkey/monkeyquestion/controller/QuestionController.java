package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


@Api(tags = "问答首页接口")
@RestController
@RequestMapping("/monkey-question/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @ApiOperation("得到最新问答列表")
    @GetMapping("/getLatestQuestionList")
    public ResultVO getQuestionList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return questionService.getLastQuestionList(currentPage, pageSize);
    }

    @ApiOperation("得到最新问答列表")
    @PostMapping("/publishQuestion")
    public ResultVO publishQuestion(@RequestParam("questionForm") @ApiParam("问答表单") String questionForm) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return questionService.publishQuestion(userId, questionForm);
    }

    @ApiOperation("得到最热问答列表")
    @GetMapping("/getHottestQuestionList")
    public ResultVO getHottestQuestionList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return questionService.getHottestQuestionList(currentPage, pageSize);
    }

    @ApiOperation("完成等你来答后端查询")
    @GetMapping("/getWaitYouQuestionList")
    public ResultVO getWaitYouQuestionList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        String userId = JwtUtil.getUserId();
        return questionService.getWaitYouQuestionList(currentPage, pageSize, userId);
    }

    @ApiOperation("得到右侧热门回答列表")
    @GetMapping("/getRightHottestQuestionList")
    public ResultVO getRightHottestQuestionList() {
        return questionService.getRightHottestQuestionList();
    }

    @ApiOperation("用过标签名模糊查询标签列表")
    @GetMapping("/getLabelListByLabelName")
    public ResultVO getLabelListByLabelName(@RequestParam("labelName") @ApiParam("模糊查询字段标签名")String labelName) {
        return questionService.getLabelListByLabelName(labelName);
    }

    @ApiOperation("问答游览数 + 1")
    @GetMapping("/questionViewCountAddOne")
    public R questionViewCountAddOne(@RequestParam("questionId") @ApiParam("问答id")Long questionId) {
        return questionService.questionViewCountAddOne(questionId);
    }

}

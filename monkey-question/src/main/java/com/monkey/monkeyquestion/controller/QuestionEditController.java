package com.monkey.monkeyquestion.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.pojo.vo.QuestionPublishVo;
import com.monkey.monkeyquestion.service.QuestionEditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 18:11
 * @version: 1.0
 * @description:
 */
@Api(tags = "问答编辑接口")
@RestController
@RequestMapping("/monkey-question/edit")
public class QuestionEditController {
    @Resource
    private QuestionEditService questionEditService;

    @ApiOperation("通过问答id查询问答信息")
    @GetMapping("/queryQuestionById")
    public R queryQuestionById(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionEditService.queryQuestionById(questionId);
    }

    @ApiOperation("更新问答信息")
    @PutMapping("/questionUpdate")
    @PreAuthorize("@questionCustomAuthority.judgeIsAuthor(#questionId)")
    public R questionUpdate(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                            @RequestParam("questionFormStr") @ApiParam("问答表单字符串") String questionFormStr) {
        QuestionPublishVo questionPublishVo = JSONObject.parseObject(questionFormStr, QuestionPublishVo.class);
        return questionEditService.questionUpdate(questionPublishVo);
    }
}

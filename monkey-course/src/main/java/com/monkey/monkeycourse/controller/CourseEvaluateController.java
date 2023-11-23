package com.monkey.monkeycourse.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseEvaluateService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/8/30 11:03
 * @version: 1.0
 * @description: 课程评价
 */
@Api(tags = "课程评价接口")
@RestController
@RequestMapping("/monkey-course/evaluate")
public class CourseEvaluateController {
    @Resource
    private CourseEvaluateService courseEvaluateService;

    @ApiOperation("得到评价标签列表")
    @GetMapping("/queryEvaluateLabelList")
    public R queryEvaluateLabelList() {
        return courseEvaluateService.queryEvaluateLabelList();
    }

    @ApiOperation("提交评价")
    @PostMapping("/submitEvaluate")
    public R submitEvaluate(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                            @RequestParam("score") @ApiParam("评价分数") Integer score,
                            @RequestParam("evaluateContent") @ApiParam("评价内容") String evaluateContent,
                            @RequestParam("selectedTags") @ApiParam("标签集合") String selectedTagsStr) {
        String userId = JwtUtil.getUserId();
        List<String> selectedTags = JSONObject.parseArray(selectedTagsStr, String.class);
        return courseEvaluateService.submitEvaluate(userId, courseId, score, evaluateContent, selectedTags);
    }
}

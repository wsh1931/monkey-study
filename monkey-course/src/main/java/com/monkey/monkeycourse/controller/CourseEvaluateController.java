package com.monkey.monkeycourse.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseEvaluateService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.temporal.JulianFields;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/30 11:03
 * @version: 1.0
 * @description: 课程评价
 */
@RestController
@RequestMapping("/monkey-course/evaluate")
public class CourseEvaluateController {
    @Resource
    private CourseEvaluateService courseEvaluateService;

    // 得到评价标签列表
    @GetMapping("/queryEvaluateLabelList")
    public R queryEvaluateLabelList() {
        return courseEvaluateService.queryEvaluateLabelList();
    }

    // 提交评价
    @PostMapping("/submitEvaluate")
    public R submitEvaluate(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        String userId = JwtUtil.getUserId();
        int score = Integer.parseInt(data.get("score"));
        String evaluateContent = data.get("evaluateContent");
        List<String> selectedTags = JSONObject.parseArray(data.get("selectedTags"), String.class);
        return courseEvaluateService.submitEvaluate(userId, courseId, score, evaluateContent, selectedTags);
    }
}

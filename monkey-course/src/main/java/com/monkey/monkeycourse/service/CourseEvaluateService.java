package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

import java.util.List;

public interface CourseEvaluateService {
    // 得到评价标签列表
    R queryEvaluateLabelList();

    // 提交评价
    R submitEvaluate(String userId, long courseId, int score, String evaluateContent, List<String> selectedTags);
}

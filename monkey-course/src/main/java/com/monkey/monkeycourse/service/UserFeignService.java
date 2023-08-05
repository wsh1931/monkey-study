package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 课程游览数 + 1
    R addCourseViewSum(Long courseId);

    // 课程游览数 - 1
    R subCourseViewSum(Long courseId);
}

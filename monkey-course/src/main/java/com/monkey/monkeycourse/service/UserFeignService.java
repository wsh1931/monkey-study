package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 课程游览数 + 1
    R addCourseViewSum(Long courseId);

    // 课程游览数 - 1
    R subCourseViewSum(Long courseId);

    // 删除用户购买课程记录
    R deleteUserBuyCourse(Long userId, Long courseId);

    // 通过课程id得到课程信息
    R queryCourseById(Long courseId);
}

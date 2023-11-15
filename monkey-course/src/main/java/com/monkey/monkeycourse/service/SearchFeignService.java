package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {
    // 查询所有课程
    R queryAllCourse();

    // 得到所有用户所有课程，点赞，收藏，游览数
    R queryAllUserCourseInfo();
}

package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeCourseService {

    // 通过用户id查询课程集合
    R queryCourseByUserId(Long userId, Long currentPage, Integer pageSize);

}

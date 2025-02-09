package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseDetailService {
    // 通过课程id得到课程信息
    R getCourseInfoByCourseId(long courseId);

    // 判断当前登录用户是否收藏该课程
    R judgeIsCollect(long courseId, String userId, int collectType);

    // 得到官方推荐课程列表
    R getCourseRecommendList();

    // 通过课程id得到教师信息
    R getUserInfoByCourseId(long courseId);

    // 通过课程id得到相关课程列表
    R getConnectCourseList(long courseId);

    // 课程游览数 + 1
    R courseViewAdd(long courseId);

    // 判断课程是否存在
    R judgeCourseIsExist(Long courseId);
}

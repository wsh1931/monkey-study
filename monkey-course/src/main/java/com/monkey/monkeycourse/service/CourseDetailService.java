package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseDetailService {
    // 通过课程id得到课程信息
    R getCourseInfoByCourseId(long courseId);
}

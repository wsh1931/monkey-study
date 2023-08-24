package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseBuyService {
    // 通过课程id得到课程信息
    R getCourseInfoByCourseId(long courseId, long userId);
}

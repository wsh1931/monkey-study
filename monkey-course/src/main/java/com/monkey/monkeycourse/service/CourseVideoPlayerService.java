package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseVideoPlayerService {
    // 通过课程id得到课程基本信息
    R getCourseInfoByCourseId(long courseId);

    // 通过课程id得到课程目录
    R getCourseDirectoryByCourseId(long courseId, String userId);

    // 用过课程视频id得到课程弹幕列表, 并格式化发送时间
    R getBarrageListByCourseVideoId(String userId, long courseVideoId);

    // 撤回2分钟内的弹幕
    R cancelBarrage(long barrageId, String userId);

    // 得到课程评价信息
    R getCourseScoreInfo(long courseId);

    // 得到评价用户集合
    R getCourseScoreUserList(long courseId, int currentPage, int pageSize);

    // 得到课程作者基本信息并判断当前登录用户是否关注他
    R getUserInfo(long userId);

    // 判断当前登录用户是否是作者粉丝
    R judgeIsFans(long userId, String nowUserId);

    // 得到最热课程列表
    R getFireCourseList(long courseId);

    // 得到该作者的其他课程
    R getTeacherOtherCourse(long teacherId);
}

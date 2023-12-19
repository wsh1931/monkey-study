package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Date;

public interface UserFeignService {
    // 课程游览数 + 1
    R courseCollectAddOne(Long courseId);

    // 课程游览数 - 1
    R courseCollectSubOne(Long courseId, Date createTime);

    // 删除用户购买课程记录
    R deleteUserBuyCourse(Long userId, Long courseId, Float money, Date payTime);

    // 通过课程id得到课程信息
    R queryCourseById(Long courseId);

    // 通过课程id, 评论id得到课程信息
    R queryCourseAndCommentById(Long courseId, Long commentId);

    // 通过课程id得到课程作者id
    Long queryCourseAuthorById(Long courseId);


    // 得到课程一周发表数
    R queryCourseCountRecentlyWeek(String userId);
}

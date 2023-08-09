package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseCommentService {
    // 得到课程评论列表
    R getCourseCommentList(long courseId, String userId);

    // 发表课程评论
    R publishCourseComment(long courseId, Long senderId, String content);

    // 课程评论点赞
    R likeCourseComment(long courseCommentId, long userId);

    // 课程评论回复功能实现
    R replyCourseComment(long senderId, long replyId, String replyContent, long courseCommentId, long courseId);

    // 删除课程评论
    R deleteCourseComment(String userId, Long courseCommentId, Long parentId);

    // 查找未回复课程评论列表
    R getUnReplyCourseComment(long courseId, String userId);

    // 得到时间评论降序/升序课程评论列表(type == 0为默认排序, type == 1为降序，type == 2为升序)
    R getDownOrUpgradeCourseComment(int type, String userId, long courseId);
}

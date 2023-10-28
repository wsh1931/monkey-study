package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.pojo.CourseComment;

public interface CourseCommentService {
    // 得到课程评论列表
    R getCourseCommentList(long courseId, String userId, Long currentPage, Long pageSize);

    // 发表课程评论
    R publishCourseComment(long courseId, Long senderId, String content);

    // 课程评论点赞
    R likeCourseComment(long courseCommentId, long userId, Long recipientId, Long courseId);

    // 课程评论回复功能实现
    R replyCourseComment(long senderId, long replyId, String replyContent, long courseCommentId, long courseId);

    // 删除课程评论
    R deleteCourseComment(Long courseCommentId, Long parentId, Long courseId);

    // 查找未回复课程评论列表
    R getUnReplyCourseComment(long courseId, String userId, Long currentPage, Long pageSize);

    // 得到时间评论降序/升序课程评论列表(type == 0为默认排序, type == 1为降序，type == 2为升序)
    R getDownOrUpgradeCourseComment(int type, String userId, long courseId, Long currentPage, Long pageSize);

    // 判断当前课程用户是否是课程作者
    R judgeIsAuthor(Long courseId, String userId);

    // 精选课程评论
    R excellentSelect(CourseComment courseComment);
}

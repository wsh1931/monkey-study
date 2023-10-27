package com.monkey.monkeycourse.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 删除课程评论
    public static final String deleteCourseComment = "deleteCourseComment";

    // 课程评论数 + 1;
    public static final String courseCommentCountAddOne = "courseCommentCountAddOne";

    // 课程评论点赞
    public static final String courseCommentLike = "courseCommentLike";

    // 更新精选课程评论
    public static final String updateCourseCurationComment = "updateCourseCurationComment";

    // 课程游览数 + 1
    public static final String courseViewCountAddOne = "courseViewCountAddOne";

    // 课程游览数 - 1
    public static final String courseViewCountSubOne = "courseViewCountSubOne";

    // 插入课程评价
    public static final String insertCourseEvaluate = "insertCourseEvaluate";

    // 插入课程支付日志
    public static final String insertCoursePayLog = "insertCoursePayLog";

    // 课程学习人数 + 1
    public static final String courseStudyPeopleAddOne = "courseStudyPeopleAddOne";

    // 更新支付订单
    public static final String updatePayOrder = "updatePayOrder";

    // 删除课程视频弹幕
    public static final String deleteCourseVideoBarrage = "deleteCourseVideoBarrage";


    // 课程支付日志
    public static final String courdePayLog = "coursePayLog";

    // 插入评论课程消息表表
    public static final String insertCommentCourseMessage = "insertCommentCourseMessage";

    // 插入回复课程消息表
    public static final String insertReplyCourseMessage = "insertReplyCourseMessage";
}

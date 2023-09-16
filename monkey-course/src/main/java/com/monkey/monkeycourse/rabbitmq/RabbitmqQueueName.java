package com.monkey.monkeycourse.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {
    // 课程视频弹幕
    public static final String COURSE_VIDEO_BARRAGE_QUEUE = "courseVideoBarrageQueue";

    // 课程视频弹幕死信队列
    public static final String COURSE_VIDEO_BARRAGE_DLX_QUEUE = "courseVideoBarrageDlxQueue";

    // 课程支付日志
    public static final String COURSE_PAY_QUEUE = "coursePayQueue";

    // 订单队列
    public static final String ORDER_QUEUE = "orderQueue";

    // 订单过期队列
    public static final String ORDER_EXPIRE_QUEUE = "orderExpireQueue";

    // 添加队列
    public static final String courseInsertQueue = "courseInsertQueue";
    // 添加死信队列
    public static final String courseInsertDlxQueue = "courseInsertDlxQueue";

    // 更新队列
    public static final String courseUpdateQueue = "courseUpdateQueue";
    // 更新死信队列
    public static final String courseUpdateDlxQueue = "courseUpdateDlxQueue";

    // 删除队列
    public static final String  courseDeleteQueue = "courseDeleteQueue";
    // 删除死信队列
    public static final String courseDeleteDlxQueue = "courseDeleteDlxQueue";
}

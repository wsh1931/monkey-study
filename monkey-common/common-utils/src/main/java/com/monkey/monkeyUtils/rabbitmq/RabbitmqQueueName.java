package com.monkey.monkeyUtils.rabbitmq;

public class RabbitmqQueueName {

    // 验证码
    public static final String EMAIL_CODE_QUEUE = "email_code_queue";

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
}

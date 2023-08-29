package com.monkey.monkeyUtils.rabbitmq;

public class RabbitmqRoutingKeyName {
    // 验证码
    public static final String EMAIL_CODE = "email_code";
    // 课程视频弹幕
    public static final String COURSE_VIDEO_BARRAGE_ROUTING = "courseVideoBarrageRouting";

    // 课程视频弹幕死信路口key
    public static final String COURSE_VIDEO_BARRAGE_DLX_ROUTING = "courseVideoBarrageDlxRouting";

    // 课程key
    public static final String COURSE_ROUTING = "courseRouting";

    // 订单key
    public static final String ORDER_ROUTING = "orderRouting";
    // 订单过期key
    public static final String ORDER_EXPIRE_ROUTING = "orderExpireRouting";
}

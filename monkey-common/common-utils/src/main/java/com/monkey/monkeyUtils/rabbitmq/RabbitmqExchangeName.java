package com.monkey.monkeyUtils.rabbitmq;

public class RabbitmqExchangeName {
    // 验证码
    public static final String EMAIL_CODE_EXCHANGE = "emailCode";

    // 课程视频弹幕
    public static final String COURSE_BARRAGE_EXCHANGE = "courseVideoBarrageExchange";

    // 课程视频弹幕死信交换机
    public static final String COURSE_BARRAGE_DLX_EXCHANGE = "courseVideoBarrageDlxExchange";

    // 课程交换机
    public static final String COURSE_DIRECT_EXCHANGE = "courseDirectExchange";
}

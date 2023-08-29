package com.monkey.monkeyUtils.rabbitmq;

import io.swagger.models.auth.In;

/**
 * @author: wusihao
 * @date: 2023/8/20 16:50
 * @version: 1.0
 * @description:
 */
public class RabbitmqExpireTime {
    // 课程弹幕过期时间单位2分钟
    public final static Integer courseBarrageExpireTime = 2 * 60 * 1000;

    // 设置订单过期时间一小时
    public static final Integer orderExpireTime = 60 * 60 * 1000;

}

package com.monkey.monkeycourse.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {
    // 课程视频弹幕
    public static final String COURSE_VIDEO_BARRAGE_ROUTING = "courseVideoBarrageRouting";

    // 课程视频弹幕死信路口key
    public static final String COURSE_VIDEO_BARRAGE_DLX_ROUTING = "courseVideoBarrageDlxRouting";

    // 支付日志路由
    public static final String PAY_LOG_ROUTING = "payLogRouting";

    // 支付日志死信路由
    public static final String PAY_LOG_DLX_ROUTING = "payLogDlxRouting";

    // 订单key
    public static final String ORDER_ROUTING = "orderRouting";
    // 订单过期key
    public static final String ORDER_EXPIRE_ROUTING = "orderExpireRouting";

    // 课程添加路由
    public static final String courseInsertRouting = "courseInsertRouting";
    // 课程添加死信路由
    public static final String courseInsertDlxRouting = "courseInsertDlxRouting";

    // 课程更新路由
    public static final String courseUpdateRouting = "courseUpdateRouting";
    // 课程更新死信路由
    public static final String courseUpdateDlxRouting = "courseUpdateDlxRouting";

    // 课程删除路由
    public static final String courseDeleteRouting = "courseDeleteRouting";
    // 课程删除死信队列
    public static final String courseDeleteDlxRouting = "courseDeleteDlxRouting";
}

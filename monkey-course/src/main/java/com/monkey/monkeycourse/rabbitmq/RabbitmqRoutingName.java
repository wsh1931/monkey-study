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

    // 课程key
    public static final String COURSE_ROUTING = "courseRouting";

    // 订单key
    public static final String ORDER_ROUTING = "orderRouting";
    // 订单过期key
    public static final String ORDER_EXPIRE_ROUTING = "orderExpireRouting";

    // 社区添加路由
    public static final String courseInsertRouting = "courseInsertRouting";
    // 社区添加死信路由
    public static final String courseInsertDlxRouting = "courseInsertDlxRouting";

    // 社区更新路由
    public static final String courseUpdateRouting = "courseUpdateRouting";
    // 社区更新死信路由
    public static final String courseUpdateDlxRouting = "courseUpdateDlxRouting";

    // 社区删除路由
    public static final String courseDeleteRouting = "courseDeleteRouting";
    // 社区删除死信队列
    public static final String courseDeleteDlxRouting = "courseDeleteDlxRouting";
}

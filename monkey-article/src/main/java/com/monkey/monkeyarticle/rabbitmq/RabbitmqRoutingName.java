package com.monkey.monkeyarticle.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // 社区添加路由
    public static final String articleInsertRouting = "articleInsertRouting";
    // 社区添加死信路由
    public static final String articleInsertDlxRouting = "articleInsertDlxRouting";

    // 社区更新路由
    public static final String articleUpdateRouting = "articleUpdateRouting";
    // 社区更新死信路由
    public static final String articleUpdateDlxRouting = "articleUpdateDlxRouting";

    // 社区删除路由
    public static final String articleDeleteRouting = "articleDeleteRouting";
    // 社区删除死信队列
    public static final String articleDeleteDlxRouting = "articleDeleteDlxRouting";
}

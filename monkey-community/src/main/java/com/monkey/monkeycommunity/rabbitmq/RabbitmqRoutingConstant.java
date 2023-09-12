package com.monkey.monkeycommunity.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingConstant {
    // 社区添加路由
    public static final String communityInsertRouting = "communityInsertRouting";
    // 社区添加死信路由
    public static final String communityInsertDlxRouting = "communityInsertDlxRouting";

    // 社区更新路由
    public static final String communityUpdateRouting = "communityUpdateRouting";
    // 社区更新死信路由
    public static final String communityUpdateDlxRouting = "communityUpdateDlxRouting";
}

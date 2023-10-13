package com.monkey.monkeyresource.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // redis更新路由
    public static final String redisUpdateRouting = "redisUpdateRouting";

    // redis更新死信路由
    public static final String redisUpdateDlxRouting = "redisUpdateDlxRouting";
    // redis死信更新路由

    // 资源添加路由
    public static final String resourceInsertRouting = "resourceInsertRouting";
    // 资源添加死信路由
    public static final String resourceInsertDlxRouting = "resourceInsertDlxRouting";

    // 资源更新路由
    public static final String resourceUpdateRouting = "resourceUpdateRouting";
    // 资源更新死信路由
    public static final String resourceUpdateDlxRouting = "resourceUpdateDlxRouting";

    // 资源删除路由
    public static final String resourceDeleteRouting = "resourceDeleteRouting";
    // 资源删除死信队列
    public static final String resourceDeleteDlxRouting = "resourceDeleteDlxRouting";
}

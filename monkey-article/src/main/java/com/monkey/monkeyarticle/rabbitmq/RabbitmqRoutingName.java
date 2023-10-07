package com.monkey.monkeyarticle.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // 文章添加路由
    public static final String articleInsertRouting = "articleInsertRouting";
    // 文章添加死信路由
    public static final String articleInsertDlxRouting = "articleInsertDlxRouting";

    // 文章更新路由
    public static final String articleUpdateRouting = "articleUpdateRouting";
    // 文章更新死信路由
    public static final String articleUpdateDlxRouting = "articleUpdateDlxRouting";

    // 文章删除路由
    public static final String articleDeleteRouting = "articleDeleteRouting";
    // 文章删除死信队列
    public static final String articleDeleteDlxRouting = "articleDeleteDlxRouting";
}

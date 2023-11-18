package com.monkey.monkeysearch.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // 资源添加路由
    public static final String searchInsertRouting = "searchInsertRouting";
    // 资源添加死信路由
    public static final String searchInsertDlxRouting = "searchInsertDlxRouting";
}

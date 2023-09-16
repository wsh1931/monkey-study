package com.monkey.monkeycommunity.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {
    // 添加队列
    public static final String communityInsertQueue = "communityInsertQueue";
    // 添加死信队列
    public static final String communityInsertDlxQueue = "communityInsertDlxQueue";

    // 更新队列
    public static final String communityUpdateQueue = "communityUpdateQueue";
    // 更新死信队列
    public static final String communityUpdateDlxQueue = "communityUpdateDlxQueue";

    // 删除队列
    public static final String  communityDeleteQueue = "communityDeleteQueue";
    // 删除死信队列
    public static final String communityDeleteDlxQueue = "communityDeleteDlxQueue";
}

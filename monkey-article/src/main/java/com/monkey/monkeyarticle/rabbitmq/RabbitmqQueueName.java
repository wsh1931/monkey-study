package com.monkey.monkeyarticle.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {

    // 添加队列
    public static final String articleInsertQueue = "articleInsertQueue";
    // 添加死信队列
    public static final String articleInsertDlxQueue = "articleInsertDlxQueue";

    // 更新队列
    public static final String articleUpdateQueue = "articleUpdateQueue";
    // 更新死信队列
    public static final String articleUpdateDlxQueue = "articleUpdateDlxQueue";

    // 删除队列
    public static final String articleDeleteQueue = "articleDeleteQueue";
    // 删除死信队列
    public static final String articleDeleteDlxQueue = "articleDeleteDlxQueue";
}

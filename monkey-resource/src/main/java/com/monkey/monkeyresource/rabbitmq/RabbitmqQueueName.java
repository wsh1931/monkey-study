package com.monkey.monkeyresource.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {

    // 添加队列
    public static final String resourceInsertQueue = "resourceInsertQueue";
    // 添加死信队列
    public static final String resourceInsertDlxQueue = "resourceInsertDlxQueue";

    // 更新队列
    public static final String resourceUpdateQueue = "resourceUpdateQueue";
    // 更新死信队列
    public static final String resourceUpdateDlxQueue = "resourceUpdateDlxQueue";

    // 删除队列
    public static final String resourceDeleteQueue = "resourceDeleteQueue";
    // 删除死信队列
    public static final String resourceDeleteDlxQueue = "resourceDeleteDlxQueue";
}

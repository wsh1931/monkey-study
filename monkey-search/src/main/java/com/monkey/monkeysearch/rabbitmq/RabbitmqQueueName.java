package com.monkey.monkeysearch.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {
    // 添加队列
    public static final String searchInsertQueue = "searchInsertQueue";
    // 添加死信队列
    public static final String searchInsertDlxQueue = "searchInsertDlxQueue";
}

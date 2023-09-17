package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqQueueName {

    // 添加队列
    public static final String questionInsertQueue = "questionInsertQueue";
    // 添加死信队列
    public static final String questionInsertDlxQueue = "questionInsertDlxQueue";

    // 更新队列
    public static final String questionUpdateQueue = "questionUpdateQueue";
    // 更新死信队列
    public static final String questionUpdateDlxQueue = "questionUpdateDlxQueue";

    // 删除队列
    public static final String questionDeleteQueue = "questionDeleteQueue";
    // 删除死信队列
    public static final String questionDeleteDlxQueue = "questionDeleteDlxQueue";
}

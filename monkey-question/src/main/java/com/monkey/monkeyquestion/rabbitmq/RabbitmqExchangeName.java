package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeName {

    // 问答添加直连交换机
    public static final String questionInsertDirectExchange = "questionInsertDirectExchange";
    // 问答添加直连死信交换机
    public static final String questionInsertDixDirectExchange = "questionInsertDixDirectExchange";

    // 问答更新直连交换机
    public static final String questionUpdateDirectExchange = "questionUpdateDirectExchange";
    // 问答更新直连死信交换机
    public static final String questionUpdateDlxDirectExchange = "questionUpdateDlxDirectExchange";

    // 问答删除直连交换机
    public static final String questionDeleteDirectExchange = "questionDeleteDirectExchange";
    // 问答删除死信直连交换机
    public static final String questionDeleteDlxDirectExchange = "questionDeleteDlxDirectExchange";
}

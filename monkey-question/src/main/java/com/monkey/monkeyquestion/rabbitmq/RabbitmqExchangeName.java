package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeName {

    // 社区添加直连交换机
    public static final String questionInsertDirectExchange = "questionInsertDirectExchange";
    // 社区添加直连死信交换机
    public static final String questionInsertDixDirectExchange = "questionInsertDixDirectExchange";

    // 文章更新直连交换机
    public static final String questionUpdateDirectExchange = "questionUpdateDirectExchange";
    // 文章更新直连死信交换机
    public static final String questionUpdateDlxDirectExchange = "questionUpdateDlxDirectExchange";

    // 文章删除直连交换机
    public static final String questionDeleteDirectExchange = "questionDeleteDirectExchange";
    // 文章删除死信直连交换机
    public static final String questionDeleteDlxDirectExchange = "questionDeleteDlxDirectExchange";
}

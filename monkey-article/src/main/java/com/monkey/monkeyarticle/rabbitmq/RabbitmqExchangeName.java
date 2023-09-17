package com.monkey.monkeyarticle.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeName {

    // 社区添加直连交换机
    public static final String articleInsertDirectExchange = "articleInsertDirectExchange";
    // 社区添加直连死信交换机
    public static final String articleInsertDixDirectExchange = "articleInsertDixDirectExchange";

    // 文章更新直连交换机
    public static final String articleUpdateDirectExchange = "articleUpdateDirectExchange";
    // 文章更新直连死信交换机
    public static final String articleUpdateDlxDirectExchange = "articleUpdateDlxDirectExchange";

    // 文章删除直连交换机
    public static final String articleDeleteDirectExchange = "articleDeleteDirectExchange";
    // 文章删除死信直连交换机
    public static final String articleDeleteDlxDirectExchange = "articleDeleteDlxDirectExchange";
}

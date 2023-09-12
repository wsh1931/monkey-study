package com.monkey.monkeycommunity.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeConstant {
    // 社区添加直连交换机
    public static final String communityInsertDirectExchange = "communityInsertDirectExchange";
    // 社区添加直连死信交换机
    public static final String communityInsertDixDirectExchange = "communityInsertDixDirectExchange";

    // 社区更新直连交换机
    public static final String communityUpdateDirectExchange = "communityUpdateDirectExchange";
    // 社区更新直连死信交换机
    public static final String communityUpdateDlxDirectExchange = "communityUpdateDlxDirectExchange";
}

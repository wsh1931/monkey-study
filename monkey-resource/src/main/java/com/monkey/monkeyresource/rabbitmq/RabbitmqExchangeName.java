package com.monkey.monkeyresource.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeName {

    // 资源添加直连交换机
    public static final String resourceInsertDirectExchange = "resourceInsertDirectExchange";
    // 资源添加直连死信交换机
    public static final String resourceInsertDixDirectExchange = "resourceInsertDixDirectExchange";

    // 资源更新直连交换机
    public static final String resourceUpdateDirectExchange = "resourceUpdateDirectExchange";
    // 资源更新直连死信交换机
    public static final String resourceUpdateDlxDirectExchange = "resourceUpdateDlxDirectExchange";

    // 资源删除直连交换机
    public static final String resourceDeleteDirectExchange = "resourceDeleteDirectExchange";
    // 资源删除死信直连交换机
    public static final String resourceDeleteDlxDirectExchange = "resourceDeleteDlxDirectExchange";
}

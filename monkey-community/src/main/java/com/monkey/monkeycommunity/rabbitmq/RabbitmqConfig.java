package com.monkey.monkeycommunity.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:50
 * @version: 1.0
 * @description:
 */
@Configuration
public class RabbitmqConfig {
    // 插入交换机

    // 定义社区添加直连交换机
    @Bean
    public DirectExchange communityAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeConstant.communityInsertDirectExchange).build();
    }

    // 定义社区添加直连死信交换机
    @Bean
    public DirectExchange communityAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeConstant.communityInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeConstant.communityInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingConstant.communityInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueConstant.communityInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueConstant.communityInsertDlxQueue).build();
    }


    // 社区直连交换机绑定添加数据队列
    @Bean
    public Binding communityDirectDlxExchangeBindingInsertQueue(DirectExchange communityAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(communityAddDirectExchange).with(RabbitmqRoutingConstant.communityInsertRouting);
    }

    // 社区直连死信交换机绑定添加死信队列
    @Bean
    public Binding communityDlxDirectExchangeBindingInsertDlxQueue(DirectExchange communityAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(communityAddDlxDirectExchange).with(RabbitmqRoutingConstant.communityInsertDlxRouting);
    }



    // 定义更新交换机

    // 定义正常直连更新交换机
    @Bean
    public DirectExchange updateDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeConstant.communityUpdateDirectExchange).build();
    }

    // 定义死信直连更新交换机
    @Bean
    public DirectExchange updateDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeConstant.communityUpdateDlxDirectExchange).build();
    }

    // 定义正常更新队列
    @Bean
    public Queue updateQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeConstant.communityUpdateDlxDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingConstant.communityUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueConstant.communityUpdateQueue).withArguments(message).build();
    }

    // 定义死信更新队列
    @Bean
    public Queue updateDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueConstant.communityUpdateDlxQueue).build();
    }

    // 正常更新交换机与正常更新队列绑定
    @Bean
    public Binding updateExchangeBindingUpdateQueue(DirectExchange updateDirectExchange, Queue updateQueue) {
        return BindingBuilder.bind(updateQueue).to(updateDirectExchange).with(RabbitmqRoutingConstant.communityUpdateRouting);
    }

    // 死信交换机与死信更新队列绑定
    @Bean
    public Binding updateDlxExchangeBindingUpdateDlxQueue(DirectExchange updateDlxDirectExchange, Queue updateDlxQueue) {
        return BindingBuilder.bind(updateDlxQueue).to(updateDlxDirectExchange).with(RabbitmqRoutingConstant.communityUpdateDlxRouting);
    }
}

package com.monkey.monkeysearch.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/20 15:39
 * @version: 1.0
 * @description:
 */
@Configuration
public class RabbitmqConfig {
    
    // 插入交换机

    // 定义搜素添加直连交换机
    @Bean
    public DirectExchange searchAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.searchInsertDirectExchange).build();
    }

    // 定义搜素添加直连死信交换机
    @Bean
    public DirectExchange searchAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.searchInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.searchInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.searchInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.searchInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.searchInsertDlxQueue).build();
    }


    // 搜素直连交换机绑定添加数据队列
    @Bean
    public Binding searchDirectDlxExchangeBindingInsertQueue(DirectExchange searchAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(searchAddDirectExchange).with(RabbitmqRoutingName.searchInsertRouting);
    }

    // 搜素直连死信交换机绑定添加死信队列
    @Bean
    public Binding searchDlxDirectExchangeBindingInsertDlxQueue(DirectExchange searchAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(searchAddDlxDirectExchange).with(RabbitmqRoutingName.searchInsertDlxRouting);
    }
    
}

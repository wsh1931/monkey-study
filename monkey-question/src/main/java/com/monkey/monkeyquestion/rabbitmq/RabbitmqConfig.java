package com.monkey.monkeyquestion.rabbitmq;

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

    // 定义问答添加直连交换机
    @Bean
    public DirectExchange courseAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionInsertDirectExchange).build();
    }

    // 定义问答添加直连死信交换机
    @Bean
    public DirectExchange courseAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.questionInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.questionInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.questionInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.questionInsertDlxQueue).build();
    }


    // 问答直连交换机绑定添加数据队列
    @Bean
    public Binding courseDirectDlxExchangeBindingInsertQueue(DirectExchange courseAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(courseAddDirectExchange).with(RabbitmqRoutingName.questionInsertRouting);
    }

    // 问答直连死信交换机绑定添加死信队列
    @Bean
    public Binding courseDlxDirectExchangeBindingInsertDlxQueue(DirectExchange courseAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(courseAddDlxDirectExchange).with(RabbitmqRoutingName.questionInsertDlxRouting);
    }

    // 定义更新交换机

    // 定义正常直连更新交换机
    @Bean
    public DirectExchange updateDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionUpdateDirectExchange).build();
    }

    // 定义死信直连更新交换机
    @Bean
    public DirectExchange updateDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionUpdateDlxDirectExchange).build();
    }

    // 定义正常更新队列
    @Bean
    public Queue updateQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.questionUpdateDlxDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.questionUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.questionUpdateQueue).withArguments(message).build();
    }

    // 定义死信更新队列
    @Bean
    public Queue updateDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.questionUpdateDlxQueue).build();
    }

    // 正常更新交换机与正常更新队列绑定
    @Bean
    public Binding updateExchangeBindingUpdateQueue(DirectExchange updateDirectExchange, Queue updateQueue) {
        return BindingBuilder.bind(updateQueue).to(updateDirectExchange).with(RabbitmqRoutingName.questionUpdateRouting);
    }

    // 死信交换机与死信更新队列绑定
    @Bean
    public Binding updateDlxExchangeBindingUpdateDlxQueue(DirectExchange updateDlxDirectExchange, Queue updateDlxQueue) {
        return BindingBuilder.bind(updateDlxQueue).to(updateDlxDirectExchange).with(RabbitmqRoutingName.questionUpdateDlxRouting);
    }


    // 定义删除交换机
    @Bean
    public DirectExchange deleteDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionDeleteDirectExchange).build();
    }

    // 定义删除死信交换机
    @Bean
    public DirectExchange deleteDlxDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.questionDeleteDlxDirectExchange).build();
    }

    // 定义删除队列
    @Bean
    public Queue deleteQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-dead-letter-exchange", RabbitmqExchangeName.questionDeleteDlxDirectExchange);
        argument.put("x-dead-letter-routing-key", RabbitmqRoutingName.questionDeleteDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.questionDeleteQueue).withArguments(argument).build();
    }

    // 定义删除死信队列
    @Bean
    public Queue deleteDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.questionDeleteDlxQueue).build();
    }

    // 绑定删除交换机与删除队列
    @Bean
    public Binding deleteExchangeBingDeleteQueue(DirectExchange deleteDeleteExchange, Queue deleteQueue) {
        return BindingBuilder.bind(deleteQueue).to(deleteDeleteExchange).with(RabbitmqRoutingName.questionDeleteRouting);
    }

    // 绑定删除死信交换机与删除死信队列
    @Bean
    public Binding deleteDlxExchangeBingDeleteDlxQueue(DirectExchange deleteDlxDeleteExchange, Queue deleteDlxQueue) {
        return BindingBuilder.bind(deleteDlxQueue).to(deleteDlxDeleteExchange).with(RabbitmqRoutingName.questionDeleteDlxRouting);
    }
}

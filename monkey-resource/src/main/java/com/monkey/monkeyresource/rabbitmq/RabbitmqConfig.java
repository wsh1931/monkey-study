package com.monkey.monkeyresource.rabbitmq;

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

    // 资源直连交换机

    // 资源直连交换机
    @Bean
    public DirectExchange resourceDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceDirectExchange).build();
    }

    // 资源直连死信队列
    @Bean
    public DirectExchange resourceDirectDlxExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceDirectDlxExchange).build();
    }

    // 更新redis队列
    @Bean
    public Queue redisUpdateQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", RabbitmqExchangeName.resourceDirectDlxExchange);
        arguments.put("x-dead-letter-routing-key", RabbitmqRoutingName.redisUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.redisUpdateQueue).withArguments(arguments).build();
    }

    // redis死信更新队列
    @Bean
    public Queue redisDlxUpdateQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.redisUpdateDlxQueue).build();
    }

    // redis更新交换机绑定更新队列
    @Bean
    public Binding redisUpdateBing(DirectExchange resourceDirectExchange, Queue redisUpdateQueue) {
        return BindingBuilder.bind(redisUpdateQueue).to(resourceDirectExchange).with(RabbitmqRoutingName.redisUpdateRouting);
    }

    // redis死信更新交换机绑定更新队列
    @Bean
    public Binding redisDlxUpdateBing(DirectExchange resourceDirectDlxExchange, Queue redisDlxUpdateQueue) {
        return BindingBuilder.bind(redisDlxUpdateQueue).to(resourceDirectDlxExchange).with(RabbitmqRoutingName.resourceUpdateDlxRouting);
    }

    // 订单正常队列
    @Bean
    public Queue resourceOrderQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", RabbitmqExchangeName.resourceDirectDlxExchange);
        arguments.put("x-dead-letter-routing-key", RabbitmqRoutingName.resourceDlxOrderRouting);
        arguments.put("x-message-ttl", RabbitmqExpireTime.orderExpireTime);
        return QueueBuilder.durable(RabbitmqQueueName.resourceOrderQueue).withArguments(arguments).build();
    }

    // 订单延迟队列
    @Bean
    public Queue resourceOrderDelayQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.resourceDelayOrderQueue).build();
    }

    // 订单正常队列绑定资源直连交换机
    @Bean
    public Binding orderQueueBingResourceDirectExchange(DirectExchange resourceDirectExchange, Queue resourceOrderQueue) {
        return BindingBuilder.bind(resourceOrderQueue).to(resourceDirectExchange).with(RabbitmqRoutingName.resourceOrderRouting);
    }

    // 订单延迟队列绑定资源死信交换机
    @Bean
    public Binding orderDelayQueueBingResourceDirectExchange(DirectExchange resourceDirectDlxExchange, Queue resourceOrderDelayQueue) {
        return BindingBuilder.bind(resourceOrderDelayQueue).to(resourceDirectDlxExchange).with(RabbitmqRoutingName.resourceDlxOrderRouting);
    }

    // 插入交换机

    // 定义资源添加直连交换机
    @Bean
    public DirectExchange courseAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceInsertDirectExchange).build();
    }

    // 定义资源添加直连死信交换机
    @Bean
    public DirectExchange courseAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.resourceInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.resourceInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.resourceInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.resourceInsertDlxQueue).build();
    }


    // 资源直连交换机绑定添加数据队列
    @Bean
    public Binding courseDirectDlxExchangeBindingInsertQueue(DirectExchange courseAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(courseAddDirectExchange).with(RabbitmqRoutingName.resourceInsertRouting);
    }

    // 资源直连死信交换机绑定添加死信队列
    @Bean
    public Binding courseDlxDirectExchangeBindingInsertDlxQueue(DirectExchange courseAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(courseAddDlxDirectExchange).with(RabbitmqRoutingName.resourceInsertDlxRouting);
    }

    // 定义更新交换机

    // 定义正常直连更新交换机
    @Bean
    public DirectExchange updateDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceUpdateDirectExchange).build();
    }

    // 定义死信直连更新交换机
    @Bean
    public DirectExchange updateDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceUpdateDlxDirectExchange).build();
    }

    // 定义正常更新队列
    @Bean
    public Queue updateQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.resourceUpdateDlxDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.resourceUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.resourceUpdateQueue).withArguments(message).build();
    }

    // 定义死信更新队列
    @Bean
    public Queue updateDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.resourceUpdateDlxQueue).build();
    }

    // 正常更新交换机与正常更新队列绑定
    @Bean
    public Binding updateExchangeBindingUpdateQueue(DirectExchange updateDirectExchange, Queue updateQueue) {
        return BindingBuilder.bind(updateQueue).to(updateDirectExchange).with(RabbitmqRoutingName.resourceUpdateRouting);
    }

    // 死信交换机与死信更新队列绑定
    @Bean
    public Binding updateDlxExchangeBindingUpdateDlxQueue(DirectExchange updateDlxDirectExchange, Queue updateDlxQueue) {
        return BindingBuilder.bind(updateDlxQueue).to(updateDlxDirectExchange).with(RabbitmqRoutingName.resourceUpdateDlxRouting);
    }


    // 定义删除交换机
    @Bean
    public DirectExchange deleteDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceDeleteDirectExchange).build();
    }

    // 定义删除死信交换机
    @Bean
    public DirectExchange deleteDlxDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.resourceDeleteDlxDirectExchange).build();
    }

    // 定义删除队列
    @Bean
    public Queue deleteQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-dead-letter-exchange", RabbitmqExchangeName.resourceDeleteDlxDirectExchange);
        argument.put("x-dead-letter-routing-key", RabbitmqRoutingName.resourceDeleteDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.resourceDeleteQueue).withArguments(argument).build();
    }

    // 定义删除死信队列
    @Bean
    public Queue deleteDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.resourceDeleteDlxQueue).build();
    }

    // 绑定删除交换机与删除队列
    @Bean
    public Binding deleteExchangeBingDeleteQueue(DirectExchange deleteDeleteExchange, Queue deleteQueue) {
        return BindingBuilder.bind(deleteQueue).to(deleteDeleteExchange).with(RabbitmqRoutingName.resourceDeleteRouting);
    }

    // 绑定删除死信交换机与删除死信队列
    @Bean
    public Binding deleteDlxExchangeBingDeleteDlxQueue(DirectExchange deleteDlxDeleteExchange, Queue deleteDlxQueue) {
        return BindingBuilder.bind(deleteDlxQueue).to(deleteDlxDeleteExchange).with(RabbitmqRoutingName.resourceDeleteDlxRouting);
    }
}

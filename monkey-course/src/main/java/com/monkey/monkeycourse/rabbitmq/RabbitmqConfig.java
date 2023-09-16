package com.monkey.monkeycourse.rabbitmq;

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

    // 正常交换机
    @Bean
    public DirectExchange courseBarrageExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.COURSE_BARRAGE_EXCHANGE).build();
    }

    // 死信交换机
    @Bean
    public DirectExchange courseBarrageDixExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.COURSE_BARRAGE_DLX_EXCHANGE).build();
    }

    // 课程交换机
    @Bean
    public DirectExchange courseExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.COURSE_DIRECT_EXCHANGE).build();
    }

    // 课程交换机队列
    @Bean
    public Queue courseQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.COURSE_PAY_QUEUE).build();
    }
    // 正常队列
    @Bean
    public Queue courseBarrageQueue() {

        Map<String, Object> arguments = new HashMap<>();
        // 设置队列中所有的消息过期时间(2分钟)
        arguments.put("x-message-ttl", RabbitmqExpireTime.courseBarrageExpireTime);
        // 当消息过期后去往死信交换机
        arguments.put("x-dead-letter-exchange", RabbitmqExchangeName.COURSE_BARRAGE_DLX_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitmqRoutingName.COURSE_VIDEO_BARRAGE_DLX_ROUTING);
        return QueueBuilder.durable(RabbitmqQueueName.COURSE_VIDEO_BARRAGE_QUEUE).withArguments(arguments).build();
    }

    // 死信队列
    @Bean
    public Queue courseBarrageDixQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.COURSE_VIDEO_BARRAGE_DLX_QUEUE).build();
    }

    // 订单队列
    @Bean
    public Queue orderQueue() {
        Map<String, Object> arguments = new HashMap<>();
        // 订单过期时间，一小时
        arguments.put("x-message-ttl", RabbitmqExpireTime.orderExpireTime);
        // 设置死信交换机，路由key
        arguments.put("x-dead-letter-exchange", RabbitmqExchangeName.COURSE_DIRECT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitmqRoutingName.ORDER_EXPIRE_ROUTING);
        return QueueBuilder.durable(RabbitmqQueueName.ORDER_QUEUE).withArguments(arguments).build();
    }

    // 订单过期队列
    @Bean
    public Queue orderExpireQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.ORDER_EXPIRE_QUEUE).build();
    }

    // 绑定订单队列
    @Bean
    public Binding bindOrderQueue(DirectExchange courseExchange, Queue orderQueue) {
        return BindingBuilder.bind(orderQueue).to(courseExchange).with(RabbitmqRoutingName.ORDER_ROUTING);
    }

    // 绑定订单过期队列
    @Bean
    public Binding bindOrderExpireQueue(DirectExchange courseExchange, Queue orderExpireQueue) {
        return BindingBuilder.bind(orderExpireQueue).to(courseExchange).with(RabbitmqRoutingName.ORDER_EXPIRE_ROUTING);
    }

    // 课程交换机队列绑定
    @Bean
    public Binding courseBind(DirectExchange courseExchange, Queue courseQueue) {
        return BindingBuilder.bind(courseQueue).to(courseExchange).with(RabbitmqRoutingName.COURSE_ROUTING);
    }



    // 正常交换机队列绑定
    @Bean
    public Binding courseBarrageBind(DirectExchange courseBarrageExchange, Queue courseBarrageQueue) {
        return BindingBuilder.bind(courseBarrageQueue).to(courseBarrageExchange).with(RabbitmqRoutingName.COURSE_VIDEO_BARRAGE_ROUTING);
    }

    // 死信交换机队列绑定
    @Bean
    public Binding courseBarrageDlxBind(DirectExchange courseBarrageDixExchange, Queue courseBarrageDixQueue) {
        return BindingBuilder.bind(courseBarrageDixQueue).to(courseBarrageDixExchange).with(RabbitmqRoutingName.COURSE_VIDEO_BARRAGE_DLX_ROUTING);
    }

    // 插入交换机

    // 定义社区添加直连交换机
    @Bean
    public DirectExchange courseAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseInsertDirectExchange).build();
    }

    // 定义社区添加直连死信交换机
    @Bean
    public DirectExchange courseAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.courseInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.courseInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.courseInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.courseInsertDlxQueue).build();
    }


    // 社区直连交换机绑定添加数据队列
    @Bean
    public Binding courseDirectDlxExchangeBindingInsertQueue(DirectExchange courseAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(courseAddDirectExchange).with(RabbitmqRoutingName.courseInsertRouting);
    }

    // 社区直连死信交换机绑定添加死信队列
    @Bean
    public Binding courseDlxDirectExchangeBindingInsertDlxQueue(DirectExchange courseAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(courseAddDlxDirectExchange).with(RabbitmqRoutingName.courseInsertDlxRouting);
    }

    // 定义更新交换机

    // 定义正常直连更新交换机
    @Bean
    public DirectExchange updateDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseUpdateDirectExchange).build();
    }

    // 定义死信直连更新交换机
    @Bean
    public DirectExchange updateDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseUpdateDlxDirectExchange).build();
    }

    // 定义正常更新队列
    @Bean
    public Queue updateQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.courseUpdateDlxDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.courseUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.courseUpdateQueue).withArguments(message).build();
    }

    // 定义死信更新队列
    @Bean
    public Queue updateDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.courseUpdateDlxQueue).build();
    }

    // 正常更新交换机与正常更新队列绑定
    @Bean
    public Binding updateExchangeBindingUpdateQueue(DirectExchange updateDirectExchange, Queue updateQueue) {
        return BindingBuilder.bind(updateQueue).to(updateDirectExchange).with(RabbitmqRoutingName.courseUpdateRouting);
    }

    // 死信交换机与死信更新队列绑定
    @Bean
    public Binding updateDlxExchangeBindingUpdateDlxQueue(DirectExchange updateDlxDirectExchange, Queue updateDlxQueue) {
        return BindingBuilder.bind(updateDlxQueue).to(updateDlxDirectExchange).with(RabbitmqRoutingName.courseUpdateDlxRouting);
    }


    // 定义删除交换机
    @Bean
    public DirectExchange deleteDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseDeleteDirectExchange).build();
    }

    // 定义删除死信交换机
    @Bean
    public DirectExchange deleteDlxDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.courseDeleteDlxDirectExchange).build();
    }

    // 定义删除队列
    @Bean
    public Queue deleteQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-dead-letter-exchange", RabbitmqExchangeName.courseDeleteDlxDirectExchange);
        argument.put("x-dead-letter-routing-key", RabbitmqRoutingName.courseDeleteDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.courseDeleteQueue).withArguments(argument).build();
    }

    // 定义删除死信队列
    @Bean
    public Queue deleteDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.courseDeleteDlxQueue).build();
    }

    // 绑定删除交换机与删除队列
    @Bean
    public Binding deleteExchangeBingDeleteQueue(DirectExchange deleteDeleteExchange, Queue deleteQueue) {
        return BindingBuilder.bind(deleteQueue).to(deleteDeleteExchange).with(RabbitmqRoutingName.courseDeleteRouting);
    }

    // 绑定删除死信交换机与删除死信队列
    @Bean
    public Binding deleteDlxExchangeBingDeleteDlxQueue(DirectExchange deleteDlxDeleteExchange, Queue deleteDlxQueue) {
        return BindingBuilder.bind(deleteDlxQueue).to(deleteDlxDeleteExchange).with(RabbitmqRoutingName.courseDeleteDlxRouting);
    }
}

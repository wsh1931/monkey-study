package com.monkey.monkeycourse.rabbitmq;

import com.monkey.monkeyUtils.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExpireTime;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqQueueName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqRoutingKeyName;
import com.monkey.monkeyUtils.result.R;
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

    // 正常队列
    @Bean
    public Queue courseBarrageQueue() {

        Map<String, Object> arguments = new HashMap<>();
        // 设置队列中所有的消息过期时间(2分钟)
        arguments.put("x-message-ttl", RabbitmqExpireTime.courseBarrageExpireTime);
        // 当消息过期后去往死信交换机
        arguments.put("x-dead-letter-exchange", RabbitmqExchangeName.COURSE_BARRAGE_DLX_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitmqRoutingKeyName.COURSE_VIDEO_BARRAGE_DLX_ROUTING);
        return QueueBuilder.durable(RabbitmqQueueName.COURSE_VIDEO_BARRAGE_QUEUE).withArguments(arguments).build();
    }

    // 死信队列
    @Bean
    public Queue courseBarrageDixQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.COURSE_VIDEO_BARRAGE_DLX_QUEUE).build();
    }

    // 正常交换机队列绑定
    @Bean
    public Binding courseBarrageBind(DirectExchange courseBarrageExchange, Queue courseBarrageQueue) {
        return BindingBuilder.bind(courseBarrageQueue).to(courseBarrageExchange).with(RabbitmqRoutingKeyName.COURSE_VIDEO_BARRAGE_ROUTING);
    }

    // 死信交换机队列绑定
    @Bean
    public Binding courseBarrageDlxBind(DirectExchange courseBarrageDixExchange, Queue courseBarrageDixQueue) {
        return BindingBuilder.bind(courseBarrageDixQueue).to(courseBarrageDixExchange).with(RabbitmqRoutingKeyName.COURSE_VIDEO_BARRAGE_DLX_ROUTING);
    }
}

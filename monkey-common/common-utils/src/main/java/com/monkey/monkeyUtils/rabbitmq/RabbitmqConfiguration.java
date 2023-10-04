package com.monkey.monkeyUtils.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/3 16:32
 * @version: 1.0
 * @description: rabbitmq配置文件
 */
@Configuration
public class RabbitmqConfiguration {
    @Resource
    private ConnectionFactory connectionFactory;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitTransactionManager rabbitTransactionManager() {
        // 设置rabbitmq支持事务
        rabbitTemplate.setChannelTransacted(true);
        return new RabbitTransactionManager(connectionFactory);
    }
}

package com.monkey.monkeyblog.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Configuration
public class RabbitmqConfig {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;

    // 验证码交换机
    @Bean
    public DirectExchange emailCodeDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE).build();
    }

    // 验证码队列
    @Bean
    public Queue emailCodeQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-dead-letter-exchange", RabbitmqExchangeName.EMAIL_CODE_EXCHANGE);
        argument.put("x-dead-letter-routing-key", RabbitmqRoutingName.EMAIL_CODE_DLX);
        return QueueBuilder.durable(RabbitmqQueueName.EMAIL_CODE_QUEUE).withArguments(argument).build();
    }

    // 验证码死信队列
    @Bean
    public Queue emailCodeDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.EMAIL_CODE_DLX_QUEUE).build();
    }
    
    // 验证码正常队列绑定
    @Bean
    public Binding emailCodeBind(DirectExchange emailCodeDirectExchange, Queue emailCodeQueue) {
        return BindingBuilder.bind(emailCodeQueue).to(emailCodeDirectExchange).with(RabbitmqRoutingName.EMAIL_CODE);
    }
    
    // 验证码死信队列绑定
    @Bean
    public Binding emailCodeDlxBind(DirectExchange emailCodeDirectExchange, Queue emailCodeDlxQueue) {
        return BindingBuilder.bind(emailCodeDlxQueue).to(emailCodeDirectExchange).with(RabbitmqRoutingName.EMAIL_CODE_DLX);
    }

    // 插入交换机

    // 定义用户添加直连交换机
    @Bean
    public DirectExchange userAddDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userInsertDirectExchange).build();
    }

    // 定义用户添加直连死信交换机
    @Bean
    public DirectExchange userAddDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userInsertDixDirectExchange).build();
    }

    // 定义添加正常队列
    @Bean
    public Queue insertQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.userInsertDixDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.userInsertDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.userInsertQueue).withArguments(message).build();
    }

    // 定义添加死信队列
    @Bean
    public Queue insertDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.userInsertDlxQueue).build();
    }


    // 用户直连交换机绑定添加数据队列
    @Bean
    public Binding userDirectDlxExchangeBindingInsertQueue(DirectExchange userAddDirectExchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(userAddDirectExchange).with(RabbitmqRoutingName.userInsertRouting);
    }

    // 用户直连死信交换机绑定添加死信队列
    @Bean
    public Binding userDlxDirectExchangeBindingInsertDlxQueue(DirectExchange userAddDlxDirectExchange, Queue insertDlxQueue) {
        return BindingBuilder.bind(insertDlxQueue).to(userAddDlxDirectExchange).with(RabbitmqRoutingName.userInsertDlxRouting);
    }



    // 定义更新交换机

    // 定义正常直连更新交换机
    @Bean
    public DirectExchange updateDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userUpdateDirectExchange).build();
    }

    // 定义死信直连更新交换机
    @Bean
    public DirectExchange updateDlxDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userUpdateDlxDirectExchange).build();
    }

    // 定义正常更新队列
    @Bean
    public Queue updateQueue() {
        Map<String, Object> message = new HashMap<>();
        message.put("x-dead-letter-exchange", RabbitmqExchangeName.userUpdateDlxDirectExchange);
        message.put("x-dead-letter-routing-key", RabbitmqRoutingName.userUpdateDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.userUpdateQueue).withArguments(message).build();
    }

    // 定义死信更新队列
    @Bean
    public Queue updateDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.userUpdateDlxQueue).build();
    }

    // 正常更新交换机与正常更新队列绑定
    @Bean
    public Binding updateExchangeBindingUpdateQueue(DirectExchange updateDirectExchange, Queue updateQueue) {
        return BindingBuilder.bind(updateQueue).to(updateDirectExchange).with(RabbitmqRoutingName.userUpdateRouting);
    }

    // 死信交换机与死信更新队列绑定
    @Bean
    public Binding updateDlxExchangeBindingUpdateDlxQueue(DirectExchange updateDlxDirectExchange, Queue updateDlxQueue) {
        return BindingBuilder.bind(updateDlxQueue).to(updateDlxDirectExchange).with(RabbitmqRoutingName.userUpdateDlxRouting);
    }


    // 定义删除交换机
    @Bean
    public DirectExchange deleteDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userDeleteDirectExchange).build();
    }

    // 定义删除死信交换机
    @Bean
    public DirectExchange deleteDlxDeleteExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.userDeleteDlxDirectExchange).build();
    }

    // 定义删除队列
    @Bean
    public Queue deleteQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-dead-letter-exchange", RabbitmqExchangeName.userDeleteDlxDirectExchange);
        argument.put("x-dead-letter-routing-key", RabbitmqRoutingName.userDeleteDlxRouting);
        return QueueBuilder.durable(RabbitmqQueueName.userDeleteQueue).withArguments(argument).build();
    }

    // 定义删除死信队列
    @Bean
    public Queue deleteDlxQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.userDeleteDlxQueue).build();
    }

    // 绑定删除交换机与删除队列
    @Bean
    public Binding deleteExchangeBingDeleteQueue(DirectExchange deleteDeleteExchange, Queue deleteQueue) {
        return BindingBuilder.bind(deleteQueue).to(deleteDeleteExchange).with(RabbitmqRoutingName.userDeleteRouting);
    }

    // 绑定删除死信交换机与删除死信队列
    @Bean
    public Binding deleteDlxExchangeBingDeleteDlxQueue(DirectExchange deleteDlxDeleteExchange, Queue deleteDlxQueue) {
        return BindingBuilder.bind(deleteDlxQueue).to(deleteDlxDeleteExchange).with(RabbitmqRoutingName.userDeleteDlxRouting);
    }

    // 发送端确认
    // 当消息发送到交换机后执行此方法
//    @PostConstruct // 当rabbitmqConfig对象创建完成只会执行此方法
    public void initRqbbitmqTemplate() {
        // 设置消息抵达或者未抵达交换机的都要进行回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                EmailCodeVo emailCodeVo = null;
                if (!ack) {
                    log.error("Returned = {}", correlationData.getReturned());
                    String exchange = correlationData.getReturned().getMessage().getMessageProperties().getReceivedExchange();
                    String routingKey = correlationData.getReturned().getMessage().getMessageProperties().getReceivedRoutingKey();
                    String receivedUserId = correlationData.getReturned().getMessage().getMessageProperties().getReceivedUserId();
                    emailCodeVo = JSONObject.parseObject(correlationData.getReturned().getMessage().getBody(), EmailCodeVo.class);
                    Integer tryCount = emailCodeVo.getTryCount();
                    // 设置消息重发
                    if (Objects.equals(tryCount, MessageReSendCount.REGISTER_EMAIL_CODE)) {
                        log.error("消息重发{}次：未到达{}交换机，原因为{}，routingKey：{}, emailCodeVo: {} 发送到交换机失败", tryCount, exchange, cause, routingKey, emailCodeVo);
                        // 传入失败日志
                        RabbitmqErrorLog messageLog = new RabbitmqErrorLog();
                        messageLog.setContent(JSONObject.toJSONString(emailCodeVo));
                        messageLog.setExchange(exchange);
                        messageLog.setErrorCause(cause);
                        messageLog.setTryCount(emailCodeVo.getTryCount());
                        messageLog.setRoutingKey(routingKey);
                        messageLog.setCreateTime(new Date());
                        int insert = rabbitmqErrorLogMapper.insert(messageLog);
                        if (insert > 0) {
                            log.error("消息发送到交换机失败次数到达预设成功加入数据库：{}", insert);
                        } else {
                            log.error("消息发送到交换机失败次数到达预设加入数据库失败：{}", insert);
                        }
                    } else {
                        if (tryCount < MessageReSendCount.REGISTER_EMAIL_CODE) {
                            emailCodeVo.setTryCount(emailCodeVo.getTryCount() + 1);
                            byte[] bytes = JSONObject.toJSONString(emailCodeVo).getBytes();
                            MessageProperties messageProperties = new MessageProperties();
                            messageProperties.setReceivedExchange(exchange);
                            messageProperties.setReceivedRoutingKey(routingKey);
                            messageProperties.setReceivedUserId(receivedUserId);
                            Message message = new Message(bytes, messageProperties);
                            log.error("第{}次重发，exchange = {}", emailCodeVo.getTryCount(), exchange);
                            CorrelationData correlationData1 = new CorrelationData();
                            ReturnedMessage returnedMessage = new ReturnedMessage(message,
                                    200,
                                    "邮箱验证码",
                                    RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
                                    RabbitmqRoutingName.EMAIL_CODE);

                            correlationData1.setReturned(returnedMessage);
                            correlationData1.setId(UUID.randomUUID().toString());
                            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData1);
                        }
                    }
                }
            }
        });

        // 设置消息未抵达队列的消息回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(@NotNull ReturnedMessage returnedMessage) {
                EmailCodeVo emailCodeVo = JSONObject.parseObject(returnedMessage.getMessage().getBody(), EmailCodeVo.class);
                log.error("returnedMessage = {}：emailCodeVo = {}", returnedMessage, emailCodeVo);
                String exchange = returnedMessage.getExchange();
                String routingKey = returnedMessage.getRoutingKey();
                Integer tryCount = emailCodeVo.getTryCount();

                if (tryCount < MessageReSendCount.REGISTER_EMAIL_CODE) {
                    emailCodeVo.setTryCount(emailCodeVo.getTryCount() + 1);
                    byte[] bytes = JSONObject.toJSONString(emailCodeVo).getBytes();
                    MessageProperties messageProperties = new MessageProperties();
                    messageProperties.setReceivedUserId(UUID.randomUUID().toString());
                    messageProperties.setReceivedExchange(exchange);
                    messageProperties.setReceivedRoutingKey(routingKey);
                    Message message = new Message(bytes, messageProperties);

                    CorrelationData correlationData = new CorrelationData();
                    correlationData.setReturned(returnedMessage);

                    rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
                } else if (tryCount.equals(MessageReSendCount.REGISTER_EMAIL_CODE)) {
                    String replyText = returnedMessage.getReplyText();
                    log.error("消息重发{}次：未到达{}队列，原因为{}||routingKey：{}|| emailCodeVo: {} 发送到交换机失败", tryCount, exchange, replyText, routingKey, emailCodeVo);
                    // 传入失败日志
                    RabbitmqErrorLog messageLog = new RabbitmqErrorLog();
                    messageLog.setContent(JSONObject.toJSONString(emailCodeVo));
                    messageLog.setExchange(exchange);
                    messageLog.setErrorCause(replyText);
                    messageLog.setTryCount(emailCodeVo.getTryCount());
                    messageLog.setRoutingKey(routingKey);
                    messageLog.setCreateTime(new Date());
                    int insert = rabbitmqErrorLogMapper.insert(messageLog);
                    if (insert > 0) {
                        log.error("消息发送到队列失败次数到达预设成功加入数据库：{}", insert);
                    } else {
                        log.error("消息发送到队列失败次数到达预设加入数据库失败：{}", insert);
                    }
                }
            }
        });
    }
}

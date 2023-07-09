package com.monkey.monkeyblog.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.mapper.ErrorMessageLogMapper;
import com.monkey.monkeyUtils.pojo.log.ErrorMessageLog;
import com.monkey.monkeyUtils.rabbitmq.MessageReSendCount;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqQueueName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqRoutingKeyName;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Configuration
public class RabbitmqConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ErrorMessageLogMapper errorMessageLogMapper;

    @Bean
    public DirectExchange emailCodeDirectExchange() {
        return ExchangeBuilder.directExchange(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE).build();
    }

    @Bean
    public Queue emailCodeQueue() {
        return QueueBuilder.durable(RabbitmqQueueName.EMAIL_CODE_QUEUE).build();
    }

    @Bean
    public Binding emailCodeBind(DirectExchange emailCodeDirectExchange, Queue emailCodeQueue) {
        return BindingBuilder.bind(emailCodeQueue).to(emailCodeDirectExchange).with(RabbitmqRoutingKeyName.EMAIL_CODE);
    }

    // 发送端确认
    // 当消息发送到交换机后执行此方法
    @PostConstruct // 当rabbitmqConfig对象创建完成只会执行此方法
    public void initRqbbitmqTemplate() {
        // 设置消息抵达或者未抵达交换机的都要进行回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                EmailCodeVo emailCodeVo = null;
                log.info("CorrelationDataId = {}", correlationData.getId());
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
                        ErrorMessageLog messageLog = new ErrorMessageLog();
                        messageLog.setContent(JSONObject.toJSONString(emailCodeVo));
                        messageLog.setExchange(exchange);
                        messageLog.setErrorCause(cause);
                        messageLog.setCorrelationDataId(UUID.randomUUID().toString());
                        messageLog.setTryCount(emailCodeVo.getTryCount());
                        messageLog.setRoutingKey(routingKey);
                        messageLog.setCreateTime(new Date());
                        int insert = errorMessageLogMapper.insert(messageLog);
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
                                    RabbitmqRoutingKeyName.EMAIL_CODE);

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
                    ErrorMessageLog messageLog = new ErrorMessageLog();
                    messageLog.setContent(JSONObject.toJSONString(emailCodeVo));
                    messageLog.setExchange(exchange);
                    messageLog.setErrorCause(replyText);
                    messageLog.setCorrelationDataId(UUID.randomUUID().toString());
                    messageLog.setTryCount(emailCodeVo.getTryCount());
                    messageLog.setRoutingKey(routingKey);
                    messageLog.setCreateTime(new Date());
                    int insert = errorMessageLogMapper.insert(messageLog);
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

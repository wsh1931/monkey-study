package com.monkey.monkeycourse.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqQueueName;
import com.monkey.monkeycourse.mapper.CourseVideoBarrageMapper;
import com.monkey.monkeycourse.pojo.CourseVideoBarrage;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/8/20 17:09
 * @version: 1.0
 * @description:
 */
@Component
public class RabbitmqReceiverMessage {

    @Autowired
    private CourseVideoBarrageMapper courseVideoBarrageMapper;
    @Autowired
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;

    @RabbitListener(queues = RabbitmqQueueName.COURSE_VIDEO_BARRAGE_QUEUE)
    public void receiverCourseBarrageMessage(Message message, Channel channel) {
        byte[] body = message.getBody();
        CourseVideoBarrage courseVideoBarrage = JSONObject.parseObject(body, CourseVideoBarrage.class);
        courseVideoBarrageMapper.insert(courseVideoBarrage);
    }

    @RabbitListener(queues = RabbitmqQueueName.COURSE_VIDEO_BARRAGE_DLX_QUEUE)
    public void receiverCourseBarrageErrorMessage(Message message, Channel channel) {
        byte[] body = message.getBody();
        CourseVideoBarrage courseVideoBarrage = JSONObject.parseObject(body, CourseVideoBarrage.class);
        courseVideoBarrageMapper.insert(courseVideoBarrage);
        // 否则插入rabbitmq错误日志
        MessageProperties messageProperties = message.getMessageProperties();
        String correlationId = messageProperties.getCorrelationId();
        String receivedExchange = messageProperties.getReceivedExchange();
        String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setCorrelationDataId(correlationId);
        rabbitmqErrorLog.setExchange(receivedExchange);
        rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
        rabbitmqErrorLog.setCreateTime(new Date());
        rabbitmqErrorLog.setContent(JSONObject.toJSONString(courseVideoBarrage));
        rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
    }
}

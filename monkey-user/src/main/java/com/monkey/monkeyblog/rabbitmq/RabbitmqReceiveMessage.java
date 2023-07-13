package com.monkey.monkeyblog.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.log.RabbitmqErrorLog;
import com.monkey.monkeyUtils.rabbitmq.MessageReSendCount;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqQueueName;
import com.monkey.monkeyblog.mapper.EmailCodeMapper;
import com.monkey.monkeyblog.pojo.EmailCode;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class RabbitmqReceiveMessage {

    @Autowired
    private EmailCodeMapper emailCodeMapper;
    @Autowired
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;

    /**
     * 把发送验证码的邮件信息存入数据库
     *
     * @param message
     * @param channel
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/6 9:06
     */
    @RabbitListener(queues = RabbitmqQueueName.EMAIL_CODE_QUEUE)
    public void receiveCodeEmailMessage(Message message, Channel channel) {
        String receivedExchange = "";
        String receivedRoutingKey = "";
        EmailCodeVo emailCodeVo = null;
        long deliveryTag = -1L;
        try {
            emailCodeVo = JSONObject.parseObject(message.getBody(), EmailCodeVo.class);
            receivedExchange = message.getMessageProperties().getReceivedExchange();
            receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
            // deliveryTag是通道内按顺序自增的
            deliveryTag = message.getMessageProperties().getDeliveryTag();

            log.info("receive_message = {} || emailCodeVo = {}", message, emailCodeVo);
            EmailCode emailCode = new EmailCode();
            BeanUtils.copyProperties(emailCodeVo, emailCode);
            int insert = emailCodeMapper.insert(emailCode);
            // 签收消息, 第二个参数启动非批量模式，是签收当前消息
            if (insert > 0) {
                // 签收成功, 自动删除消息
                log.info("已签收到消息：{}", emailCode);
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("消息签收发送错误：{}", emailCodeVo);
            }
        } catch (Exception e) {
            // 发送异常，拒绝接收消息
            // 第一个参数，消息编号
            // 第二个参数，是否批量处理消息
            // 第三个消息是否重新把消息放入队列对头
            // channel.basicNack(deliveryTag, false, true);
            /*
            我们不使用上面的方法回传参数，因为上面的方法会将消息重新放入队头，
            而我们需要将消息重新放回队尾，并设置重传机制
            怎么做呢？？？
            只要把这个消息确认，然后利用rabittemplate重新发送一次消息就可以插入队尾了
            * */
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Integer tryCount = emailCodeVo.getTryCount();
            if (tryCount < MessageReSendCount.REGISTER_EMAIL_CODE) {
                // 重传消息
                emailCodeVo.setTryCount(emailCodeVo.getTryCount() + 1);
                log.error("消息接收正在进行第：{}次重传", emailCodeVo.getTryCount());
                String str = JSONObject.toJSONString(emailCodeVo);
                log.error("str = {}", str);
                try {
                    channel.basicPublish(receivedExchange, receivedRoutingKey, null, str.getBytes());
                } catch (IOException ex) {
                    log.error("消息：{}重新发送失败", emailCodeVo);
                }
            } else {
                // 已超过重传次数，将消息放入错误日志中
                RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
                rabbitmqErrorLog.setTryCount(tryCount);
                rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
                rabbitmqErrorLog.setContent(JSONObject.toJSONString(emailCodeVo));
                rabbitmqErrorLog.setCorrelationDataId(UUID.randomUUID().toString());
                rabbitmqErrorLog.setExchange(receivedExchange);
                rabbitmqErrorLog.setCreateTime(new Date());
                rabbitmqErrorLog.setErrorCause(e.toString());
                int insert = rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
                if (insert > 0) {
                    log.error("插入错误日志成功：{}", rabbitmqErrorLog);
                } else {
                    log.error("插入日志失败：{}", rabbitmqErrorLog);
                }
            }
        }
    }
}

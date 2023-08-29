package com.monkey.monkeycourse.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.internal.LinkedTreeMap;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.AliPayTradeStatusEnum;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.mapper.OrderLogMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.pojo.OrderLog;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExpireTime;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqQueueName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqRoutingKeyName;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseVideoBarrageMapper;
import com.monkey.monkeycourse.pojo.CourseVideoBarrage;
import com.monkey.monkeycourse.service.CoursePayService;
import com.monkey.monkeycourse.service.impl.CoursePayServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static com.monkey.monkeyUtils.util.DateUtils.*;

/**
 * @author: wusihao
 * @date: 2023/8/20 17:09
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class RabbitmqReceiverMessage {

    @Autowired
    private CourseVideoBarrageMapper courseVideoBarrageMapper;
    @Autowired
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private CoursePayService coursePayService;

    @Autowired
    private OrderInformationMapper orderInformationMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    // 接收课程支付记录
    @RabbitListener(queues = RabbitmqQueueName.COURSE_PAY_QUEUE)
    public void receiverCoursePayRecords(Message message, Channel channel) {
        byte[] body = message.getBody();
        OrderLog orderLog = JSONObject.parseObject(body, OrderLog.class);
        orderLogMapper.insert(orderLog);
    }

    /**
     * 查询一小时后的订单状态
     * 根据订单号调用支付宝查单金额接口，核实订单状态
     * 如果订单未创建。则更新商户端订单状态
     * 如何订单未支付，则调用关闭订单接口，并更新订单状态
     * 如果订单已支付，则更新订单状态，记录支付日志（可能是因为支付宝支付成功请求发送失败，或内网穿透失败）
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 17:36
     */
    @RabbitListener(queues = RabbitmqQueueName.ORDER_EXPIRE_QUEUE)
    public void receiverOrderRecords(Message message, Channel channel) {
        byte[] body = message.getBody();
        OrderInformation orderInformation = JSONObject.parseObject(body, OrderInformation.class);

        Long orderInformationId = orderInformation.getId();
        log.info("查询一小时后的订单状态：==> orderInformationId = {}", orderInformation);
        String result = coursePayService.queryOrder(orderInformationId);
        if (result == null) {
            log.warn("订单未创建： ==> {}", orderInformationId);
        } else {
            // 否则解析阿里云的相应结果
            Gson gson = new Gson();
            HashMap<String, LinkedTreeMap> hashMap = gson.fromJson(result, HashMap.class);
            LinkedTreeMap alipayTradeQueryResponse = hashMap.get("alipay_trade_query_response");
            String tradeStatus = (String) alipayTradeQueryResponse.get("trade_status");
            if (AliPayTradeStatusEnum.WAIT_BUYER_PAY.getStatus().equals(tradeStatus)) {
                log.warn("核实订单未支付 ===> {}", orderInformationId);

                // 若未支付，调用阿里云关单接口
                coursePayService.closeOrder(orderInformationId);
                // 更新订单状态。
                orderInformation.setOrderStatus(CommonEnum.EXCEED_TIME_AlREADY_CLOSE.getMsg());
                orderInformationMapper.updateById(orderInformation);
            } else if (AliPayTradeStatusEnum.TRADE_SUCCESS.getStatus().equals(tradeStatus)) {
                // 如果订单已支付，则更新订单状态（可能是因为支付宝支付成功请求发送失败，或内网穿透失败）
                log.info("核实订单已支付 ==> {}", orderInformationId);
                OrderInformation information = orderInformationMapper.selectById(orderInformationId);
                String orderStatus = information.getOrderStatus();

                if (!CommonEnum.WAIT_EVALUATE.getMsg().equals(orderStatus)) {
                    // 更新订单接口
                    information.setOrderStatus(CommonEnum.WAIT_EVALUATE.getMsg());
                    orderInformationMapper.updateById(information);

                    // 记录日志
                    String sendPayDate = (String) alipayTradeQueryResponse.get("send_pay_date");
                    Date payTime = stringToDate(sendPayDate, DATE_TIME_PATTERN);
                    Date gmtCreate = addDateSeconds(new Date(), RabbitmqExpireTime.orderExpireTime / 1000);
                    String createTime = format(gmtCreate, DATE_TIME_PATTERN);
                    Date date = stringToDate(createTime, DATE_TIME_PATTERN);

                    String totalAmount = (String) alipayTradeQueryResponse.get("total_amount");
                    String outTradeNo = (String) alipayTradeQueryResponse.get("out_trade_no");
                    String tradeStatus1 = (String) alipayTradeQueryResponse.get("trade_status");
                    String tradeNo = (String) alipayTradeQueryResponse.get("trade_no");
                    String dataJson = JSONObject.toJSONString(alipayTradeQueryResponse);
                    OrderLog orderLog = new OrderLog();
                    orderLog.setId(Long.parseLong(outTradeNo));

                    orderLog.setTradeStatus(tradeStatus1);
                    orderLog.setTradeType(CommonEnum.COMPUTER_PAY.getMsg());
                    orderLog.setPayType(CommonEnum.ALIPAY.getMsg());

                    orderLog.setNoticeParams(dataJson);

                    orderLog.setTransactionId(tradeNo);
                    orderLog.setCreateTime(date);
                    orderLog.setPayTime(payTime);
                    orderLog.setPayMoney(Float.parseFloat(totalAmount));

                    byte[] bytes = JSONObject.toJSONString(orderLog).getBytes();
                    Message message1 = new Message(bytes);
                    // 通过消息队列更新日志
                    rabbitTemplate.convertAndSend(RabbitmqExchangeName.COURSE_DIRECT_EXCHANGE,
                            RabbitmqRoutingKeyName.COURSE_ROUTING, message1);
                }
            }
        }
    }
}

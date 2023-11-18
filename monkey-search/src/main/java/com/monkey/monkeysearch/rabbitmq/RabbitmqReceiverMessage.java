package com.monkey.monkeysearch.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
import com.monkey.monkeysearch.mapper.SearchHistoryMapper;
import com.monkey.monkeysearch.pojo.SearchHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: wusihao
 * @date: 2023/8/20 17:09
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class RabbitmqReceiverMessage {

    @Resource
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;
    @Resource
    private SearchHistoryMapper searchHistoryMapper;

    // 搜素模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.searchInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("搜素模块rabbitmq插入队列：event ==> {}", event);
            if (EventConstant.insertSearchHistory.equals(event)) {
                log.info("将搜索信息插入历史搜素记录");
                String userId = data.getString("userId");
                String keyword = data.getString("keyword");
                this.insertSearchHistory(userId, keyword);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 搜素模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.searchInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("搜素模块rabbitmq死信插入队列：event ==> {}", event);
            if (EventConstant.insertSearchHistory.equals(event)) {
                log.info("将搜索信息插入历史搜素记录");
                String userId = data.getString("userId");
                String keyword = data.getString("keyword");
                this.insertSearchHistory(userId, keyword);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 将搜索信息插入历史搜素记录
     *
     * @param userId 用户id
     * @param keyword 插入搜素关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/18 14:03
     */
    private void insertSearchHistory(String userId, String keyword) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setContent(keyword);
        searchHistory.setUserId(Long.parseLong(userId));
        searchHistory.setCreateTime(new Date());
        searchHistoryMapper.insert(searchHistory);
    }

    /**
     * 将错误信息放入rabbitmq错误日志
     *
     * @param message rabbitmq的消息
     * @param e  错误的异常情况
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 7:57
     */
    private void addToRabbitmqErrorLog(Message message, Exception e) {
        MessageProperties messageProperties = message.getMessageProperties();
        String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
        String receivedExchange = messageProperties.getReceivedExchange();
        JSONObject jsonObject = JSONObject.parseObject(message.getBody(), JSONObject.class);
        String event = jsonObject.getString("event");
        log.error("发送错误事件: event ==> {}, 错误原因为 ==> {}", event, e.getMessage());
        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setContent(jsonObject.toJSONString());
        rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
        rabbitmqErrorLog.setExchange(receivedExchange);
        rabbitmqErrorLog.setCreateTime(new Date());
        rabbitmqErrorLog.setErrorCause(e.getMessage());
        rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
    }
}

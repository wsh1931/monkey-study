package com.monkey.monkeycommunity.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.EventConstant;
import com.monkey.monkeycommunity.mapper.CommunityArticleTaskMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleVetoItemMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleVetoMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityArticleTask;
import com.monkey.monkeycommunity.pojo.CommunityArticleVeto;
import com.monkey.monkeycommunity.pojo.CommunityArticleVetoItem;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:13
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class RabbitmqReceiverMessage {
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;
    @Resource
    private CommunityArticleVetoMapper communityArticleVetoMapper;
    @Resource
    private CommunityArticleVetoItemMapper communityArticleVetoItemMapper;
    @Resource
    private CommunityArticleTaskMapper communityArticleTaskMapper;

    // 正常更新队列
    @RabbitListener(queues = RabbitmqQueueConstant.communityUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            if (EventConstant.communityUpdateArticleCount.equals(event)) {
                // 社区文章数 + 1;
                communityArticleCountAddOne(jsonObject);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 死信更新队列
    @RabbitListener(queues = RabbitmqQueueConstant.communityUpdateDlxQueue)
    public void receiverUpdateDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            if (EventConstant.communityUpdateArticleCount.equals(event)) {
                // 社区文章数 + 1;
                communityArticleCountAddOne(jsonObject);

            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 正常插入队列
    @RabbitListener(queues = RabbitmqQueueConstant.communityInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            log.info("正常插入队列 ==》 event：{}", event);
            if (event.equals(EventConstant.insertArticleVeto)) {
                // 插入文章投票表
                CommunityArticleVeto communityArticleVeto = JSONObject.parseObject(jsonObject.getString("communityArticleVeto"), CommunityArticleVeto.class);
                insertToArticleVeto(communityArticleVeto);
            } else if (event.equals(EventConstant.insertArticleTask)) {
                // 插入文章任务表
                List<User> communityMemberList = JSONObject.parseArray(jsonObject.getString("communityMemberList"), User.class);
                CommunityArticleTask communityArticleTask = JSONObject.parseObject(jsonObject.getString("communityArticleTask"), CommunityArticleTask.class);
                insertToArticleTask(communityArticleTask, communityMemberList);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 插入文章任务表
     *
     * @param communityArticleTask 文章任务实体类
     * @param communityMemberList 社区成员集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 8:56
     */
    private void insertToArticleTask(CommunityArticleTask communityArticleTask, List<User> communityMemberList) {
        log.info("正常插入队列任务信息 ==》 communityArticleTask：{}", communityArticleTask);
        log.info("正常插入队列成员信息 ==》 communityMemberList：{}", communityMemberList);
        Date date = new Date();
        communityArticleTask.setCreateTime(date);
        communityArticleTask.setUpdateTime(date);
        if (communityMemberList != null) {
            String userIds = "";
            int communityMemberLen = communityMemberList.size();
            for (int i = 0; i < communityMemberLen; i ++ ) {
                if (i == communityMemberLen - 1) {
                    userIds += communityMemberList.get(i).getId();
                } else {
                    userIds += communityMemberList.get(i).getId() + ",";
                }
            }
            communityArticleTask.setUserIds(userIds);
        }

        communityArticleTaskMapper.insert(communityArticleTask);
    }

    // 正常插入死信队列
    @RabbitListener(queues = RabbitmqQueueConstant.communityInsertDlxQueue)
    public void receiverInsertDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            if (event.equals(EventConstant.insertArticleVeto)) {
                // 插入文章投票表
                CommunityArticleVeto communityArticleVeto = JSONObject.parseObject(jsonObject.getString("communityArticleVeto"), CommunityArticleVeto.class);
                insertToArticleVeto(communityArticleVeto);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 插入文章投票表
     *
     * @param communityArticleVeto 文章投票实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 8:45
     */
    private void insertToArticleVeto(CommunityArticleVeto communityArticleVeto) {
        log.info("正常插入队列任务投票信息 ==》 communityArticleVeto：{}", communityArticleVeto);
        Date date = new Date();
        List<CommunityArticleVetoItem> communityArticleVetoItemList = communityArticleVeto.getCommunityArticleVetoItemList();
        int communityArticleVetoLen = communityArticleVetoItemList.size();
        communityArticleVeto.setVetoPeople(communityArticleVetoLen);
        communityArticleVeto.setUpdateTime(date);
        communityArticleVeto.setCreateTime(date);
        communityArticleVetoMapper.insert(communityArticleVeto);
        Long communityArticleVetoId = communityArticleVeto.getId();

        for (CommunityArticleVetoItem communityArticleVetoItem : communityArticleVetoItemList) {
            communityArticleVetoItem.setCommunityArticleVetoId(communityArticleVetoId);
            communityArticleVetoItem.setCreateTime(date);
            communityArticleVetoItemMapper.insert(communityArticleVetoItem);
        }
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
        String correlationId = messageProperties.getCorrelationId();
        String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
        String receivedExchange = messageProperties.getReceivedExchange();
        byte[] body = message.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);

        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setContent(jsonObject.toJSONString());
        rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
        rabbitmqErrorLog.setExchange(receivedExchange);
        rabbitmqErrorLog.setCreateTime(new Date());
        rabbitmqErrorLog.setErrorCause(e.getMessage());
        rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
    }

    /**
     * 社区文章数 + 1
     *
     * @param jsonObject rabbitmq传过来的数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 7:55
     */
    private void communityArticleCountAddOne(JSONObject jsonObject) {
        Long communityId = jsonObject.getLong("communityId");
        UpdateWrapper<Community> communityUpdateWrapper = new UpdateWrapper<>();
        communityUpdateWrapper.eq("id", communityId);
        communityUpdateWrapper.setSql("article_count = article_count + 1");
        communityMapper.update(null, communityUpdateWrapper);
    }
}

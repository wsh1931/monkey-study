package com.monkey.monkeycommunity.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
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
    @Resource
    private CommunityArticleMapper communityArticleMapper;

    // 正常更新队列
    @RabbitListener(queues = RabbitmqQueueName.communityUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            log.info("正常更新队列时间 ==> event : {}", event);
            if (EventConstant.communityArticleCountAddOne.equals(event)) {
                // 社区文章数 + 1;
                communityArticleCountAddOne(jsonObject);
            } else if (EventConstant.communityMemberCountAddOne.equals(event)) {
                // 社区成员数 + 1
                Long communityId = jsonObject.getLong("communityId");
                communityMemberCountAddone(communityId);
            } else if (EventConstant.getCommunityMemberCountSubOne.equals(event)) {
                // 社区成员数 - 1
                Long communityId = jsonObject.getLong("communityId");
                communityMemberCountSubOne(communityId);
            } else if (EventConstant.communityArticleCountSubOne.equals(event)) {
                Long communityId = jsonObject.getLong("communityId");
                log.info("社区文章数减一：communityId：{}", communityId);
                communityArticleCountSubOne(communityId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.communityUpdateDlxQueue)
    public void receiverUpdateDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            String event = jsonObject.getString("event");
            log.info("死信更新队列时间 ==> event : {}", event);
            if (EventConstant.communityArticleCountAddOne.equals(event)) {
                // 社区文章数 + 1;
                communityArticleCountAddOne(jsonObject);
            } else if (EventConstant.communityMemberCountAddOne.equals(event)) {
                // 社区成员数 + 1
                Long communityId = jsonObject.getLong("communityId");
                communityMemberCountAddone(communityId);
            } else if (EventConstant.getCommunityMemberCountSubOne.equals(event)) {
                // 社区成员数 - 1
                Long communityId = jsonObject.getLong("communityId");
                communityMemberCountSubOne(communityId);
            } else if (EventConstant.communityArticleCountSubOne.equals(event)) {
                Long communityId = jsonObject.getLong("communityId");
                log.info("社区文章数减一：communityId：{}", communityId);
                communityArticleCountSubOne(communityId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 正常插入队列
    @RabbitListener(queues = RabbitmqQueueName.communityInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            String event = data.getString("event");
            log.info("正常插入队列 ==》 event：{}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                CommunityArticleVo communityArticleVo = JSONObject.parseObject(data.getString("communityArticleVo"), CommunityArticleVo.class);
                Long communityId = data.getLong("communityId");
                Long userId = data.getLong("userId");
                publishArticle(userId, communityId, communityArticleVo);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.communityInsertDlxQueue)
    public void receiverInsertDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            String event = data.getString("event");
            log.info("死信插入队列 ==》 event：{}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                CommunityArticleVo communityArticleVo = JSONObject.parseObject(data.getString("communityArticleVo"), CommunityArticleVo.class);
                Long communityId = data.getLong("communityId");
                Long userId = data.getLong("userId");
                publishArticle(userId, communityId, communityArticleVo);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 正常删除队列
    @RabbitListener(queues = RabbitmqQueueName.communityDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(message.getBody());
            String event = jsonObject.getString("event");
            log.info("正常删除队列: event ==> {}", event);
            if (event.equals(EventConstant.deleteCommunityArticle)) {
                // 删除社区文章
                Long communityArticleId = jsonObject.getLong("communityArticleId");
                deleteCommunityArticle(communityArticleId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.communityDeleteDlxQueue)
    public void reveiverDeleteDlxQueue(Message message) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(message.getBody());
            String event = jsonObject.getString("event");
            log.info("死信删除队列: event ==> {}", event);
            if (event.equals(EventConstant.deleteCommunityArticle)) {
                // 删除社区文章
                Long communityArticleId = jsonObject.getLong("communityArticleId");
                deleteCommunityArticle(communityArticleId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除社区文章
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 17:33
     */
    private void deleteCommunityArticle(Long communityArticleId) {
        communityArticleMapper.deleteById(communityArticleId);
    }

    /**
     * 社区文章数 - 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 17:33
     */
    private void communityArticleCountSubOne(Long communityId) {
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.setSql("article_count = article_count - 1");
        communityMapper.update(null, updateWrapper);
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

    /**
     * 插入文章投票表
     *
     * @param communityArticleVeto 文章投票实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 8:45
     */
    private void insertToArticleVeto(CommunityArticleVeto communityArticleVeto) {
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

    /**
     * 社区成员数 - 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 16:11
     */
    private void communityMemberCountSubOne(Long communityId) {
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.setSql("people_count = people_count - 1");
        communityMapper.update(null, updateWrapper);
    }

    /**
     * 社区成员数 + 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 16:11
     */
    private void communityMemberCountAddone(Long communityId) {
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.setSql("people_count = people_count + 1");
        communityMapper.update(null, updateWrapper);
    }

    /**
     * 发布社区文章
     *
     * @param userId 当前登录用户id
     * @param communityId 社区id
     * @param communityArticleVo 社区文章实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 9:16
     */
    private void publishArticle(Long userId, Long communityId, CommunityArticleVo communityArticleVo) {
        // 当前时间
        Date nowDate = new Date();

        // 插入社区文章表
        CommunityArticle communityArticle = new CommunityArticle();
        BeanUtils.copyProperties(communityArticleVo, communityArticle);

        communityArticle.setCommunityId(communityId);
        communityArticle.setUserId(userId);
        communityArticle.setChannelId(communityArticleVo.getChannelId());
        communityArticle.setPicture(communityArticleVo.getPicture());
        communityArticle.setIsTask(communityArticleVo.getIsTask());
        communityArticle.setIsVote(communityArticleVo.getIsVote());
        communityArticle.setStatus(CommunityEnum.REVIEW_PROGRESS.getCode());
        communityArticle.setCreateTime(nowDate);
        communityArticle.setUpdateTime(nowDate);

        communityArticleMapper.insert(communityArticle);
        Long articleId = communityArticle.getId();

        // 插入文章任务表
        if (communityArticle.getIsTask().equals(CommunityEnum.IS_TASK.getCode())) {
            CommunityArticleTask communityArticleTask = communityArticleVo.getCommunityArticleTask();
            List<User> communityMemberList = communityArticleVo.getCommunityMemberList();
            communityArticleTask.setCommunityArticleId(articleId);
            insertToArticleTask(communityArticleTask, communityMemberList);
        }

        // 插入文章投票表
        if (communityArticle.getIsVote().equals(CommunityEnum.IS_VOTE.getCode())) {
            CommunityArticleVeto communityArticleVeto = communityArticleVo.getCommunityArticleVeto();
            communityArticleVeto.setCommunityArticleId(articleId);
            insertToArticleVeto(communityArticleVeto);
        }
    }
}

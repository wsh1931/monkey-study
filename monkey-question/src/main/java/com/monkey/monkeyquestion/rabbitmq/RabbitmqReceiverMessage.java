package com.monkey.monkeyquestion.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

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
    private QuestionMapper questionMapper;
    @Resource
    private QuestionReplyMapper questionReplyMapper;


    // 问答模块rabbitmq删除队列
    @RabbitListener(queues = RabbitmqQueueName.questionDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 问答模块rabbitmq死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.questionDeleteDlxQueue)
    public void receiverDlxDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq死信删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    // 问答模块rabbitmq更新队列
    @RabbitListener(queues = RabbitmqQueueName.questionUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq更新队列：event ==> {}", event);
            if (EventConstant.questionViewsAddOne.equals(event)) {
                // 问答游览数 + 1
                Long questionId = data.getLong("questionId");
                questionViewsAddOne(questionId);
            } else if (EventConstant.questionCollectCountAddOne.equals(event)) {
                // 问答收藏数 + 1
                Long questionId = data.getLong("questionId");
                questionCollectCountAddOne(questionId);
            } else if (EventConstant.questionCollectCountSubOne.equals(event)) {
                // 问答收藏数 - 1
                Long questionId = data.getLong("questionId");
                questionCollectCountSubOne(questionId);
            } else if (EventConstant.questionReplyCountAddOne.equals(event)) {
                // 问答回复数 + 1（问答回复表）
                Long questionReplyId = data.getLong("questionReplyId");
                questionReplyCountAddOne(questionReplyId);
            } else if (EventConstant.questionLikeCountAddOne.equals(event)) {
                // 问答点赞数 + 1
                Long questionId = data.getLong("questionId");
                questionLikeCountAddOne(questionId);
            } else if (EventConstant.questionLikeCountSubOne.equals(event)) {
                // 问答点赞数 - 1
                Long questionId = data.getLong("questionId");
                questionLikeCountSubOne(questionId);
            } else if (EventConstant.questionReplyCountAdd.equals(event)) {
                // 问答回复数 + 1（问答表）
                Long questionId = data.getLong("questionId");
                questionReplyCountAdd(questionId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答回复数 + 1（问答表）
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:41
     */
    private void questionReplyCountAdd(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("reply_count = reply_count + 1");
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 问答点赞数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:33
     */
    private void questionLikeCountAddOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("like_count = like_count + 1");
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 问答点赞数 - 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:33
     */
    private void questionLikeCountSubOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("like_count = like_count - 1");
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 问答回复数 + 1
     *
     * @param questionReplyId 问答回复id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:18
     */
    private void questionReplyCountAddOne(Long questionReplyId) {
        UpdateWrapper<QuestionReply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionReplyId);
        updateWrapper.setSql("question_reply_count = question_reply_count + 1");
        questionReplyMapper.update(null, updateWrapper);
    }

    /**
     * 问答收藏数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:06
     */
    private void questionCollectCountAddOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("collect_count = collect_count + 1");
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 问答收藏数 - 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 15:06
     */
    private void questionCollectCountSubOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("collect_count = collect_count - 1");
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 问答游览数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 14:53
     */
    private void questionViewsAddOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("visit = visit + 1");
        questionMapper.update(null, updateWrapper);
    }


    // 问答模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.questionUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq死信更新队列：event ==> {}", event);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }


    // 问答模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.questionInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq插入队列：event ==> {}", event);

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }


    // 问答模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.questionInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq死信插入队列：event ==> {}", event);

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
            throw new MonkeyBlogException(R.Error, e.getMessage());
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
        JSONObject jsonObject = JSONObject.parseObject(message.getBody(), JSONObject.class);

        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setContent(jsonObject.toJSONString());
        rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
        rabbitmqErrorLog.setExchange(receivedExchange);
        rabbitmqErrorLog.setCreateTime(new Date());
        rabbitmqErrorLog.setErrorCause(e.getMessage());
        rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
    }
}

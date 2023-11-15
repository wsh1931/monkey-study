package com.monkey.monkeyquestion.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.mapper.MessageCommentReplyMapper;
import com.monkey.monkeyUtils.mapper.MessageLikeMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.pojo.MessageLike;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyquestion.constant.QuestionPictureEnum;
import com.monkey.monkeyquestion.feign.QuestionToSearchFeignService;
import com.monkey.monkeyquestion.mapper.QuestionLabelMapper;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionLabel;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private QuestionToSearchFeignService questionToSearchFeignService;
    @Resource
    private QuestionLabelMapper questionLabelMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LabelMapper labelMapper;


    // 问答模块rabbitmq删除队列
    @RabbitListener(queues = RabbitmqQueueName.questionDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            this.addToRabbitmqErrorLog(message, e);
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
                this.questionViewsAddOne(questionId);
            } else if (EventConstant.questionCollectCountAddOne.equals(event)) {
                // 问答收藏数 + 1
                Long questionId = data.getLong("questionId");
                this.questionCollectCountAddOne(questionId);
            } else if (EventConstant.questionCollectCountSubOne.equals(event)) {
                // 问答收藏数 - 1
                Long questionId = data.getLong("questionId");
                this.questionCollectCountSubOne(questionId);
            } else if (EventConstant.questionReplyCountAddOne.equals(event)) {
                // 问答回复数 + 1（问答回复表）
                Long questionReplyId = data.getLong("questionReplyId");
                this.questionReplyCountAddOne(questionReplyId);
            } else if (EventConstant.questionLikeCountAddOne.equals(event)) {
                // 问答点赞数 + 1
                Long questionId = data.getLong("questionId");
                this.questionLikeCountAddOne(questionId);
            } else if (EventConstant.questionLikeCountSubOne.equals(event)) {
                // 问答点赞数 - 1
                Long questionId = data.getLong("questionId");
                this.questionLikeCountSubOne(questionId);
            } else if (EventConstant.questionReplyCountAdd.equals(event)) {
                // 问答回复数 + 1（问答表）
                Long questionId = data.getLong("questionId");
                this.questionReplyCountAdd(questionId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }



    // 问答模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.questionUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq死信更新队列：event ==> {}", event);
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
        }
    }


    // 问答模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.questionInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq插入队列：event ==> {}", event);
             if (EventConstant.insertCommentQuestionMessage.equals(event)) {
                log.info("插入评论问答消息表表");
                Long questionId = data.getLong("questionId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.insertCommentQuestionMessage(questionId, senderId, commentContent);
            } else if (EventConstant.insertReplyQuestionMessage.equals(event)) {
                log.info("插入回复问答消息表");
                Long questionId = data.getLong("questionId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                 Long commentId = data.getLong("commentId");
                 this.insertReplyQuestionMessage(questionId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertQuestionLikeContentMessage.equals(event)) {
                 log.info("插入文章消息点赞表");
                 Long associationId = data.getLong("associationId");
                 Long senderId = data.getLong("senderId");
                 Long recipientId = data.getLong("recipientId");
                 this.insertLikeContentMessage(associationId, senderId, recipientId);
             } else if (EventConstant.inserElasticsearchQuesion.equals(event)) {
                 log.info("插入elasticsearch问答表中");
                 String questionStr = data.getString("questionStr");
                 this.insertElasticsearchQuesion(questionStr);
             }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }



    // 问答模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.questionInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("问答模块rabbitmq死信插入队列：event ==> {}", event);
            if (EventConstant.insertCommentQuestionMessage.equals(event)) {
                log.info("插入评论问答消息表表");
                Long questionId = data.getLong("questionId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.insertCommentQuestionMessage(questionId, senderId, commentContent);
            } else if (EventConstant.insertReplyQuestionMessage.equals(event)) {
                log.info("插入回复问答消息表");
                Long questionId = data.getLong("questionId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.insertReplyQuestionMessage(questionId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertQuestionLikeContentMessage.equals(event)) {
                log.info("插入文章消息点赞表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                this.insertLikeContentMessage(associationId, senderId, recipientId);
            } else if (EventConstant.inserElasticsearchQuesion.equals(event)) {
                log.info("插入elasticsearch问答表中");
                String questionStr = data.getString("questionStr");
                this.insertElasticsearchQuesion(questionStr);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 插入elasticsearch问答表中
     *
     * @param questionStr 问答实体类字符串
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 15:56
     */
    private void insertElasticsearchQuesion(String questionStr) {
        Question question = JSONObject.parseObject(questionStr, Question.class);
        Long questionId = question.getId();
        // 得到问答标签信息
        QueryWrapper<QuestionLabel> questionLabelQueryWrapper = new QueryWrapper<>();
        questionLabelQueryWrapper.eq("question_id", questionId);
        questionLabelQueryWrapper.select("label_id");
        List<Object> labelIds = questionLabelMapper.selectObjs(questionLabelQueryWrapper);
        if (labelIds != null && labelIds.size() > 0) {
            QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
            labelQueryWrapper.in("id", labelIds);
            List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
            List<String> labelName = new ArrayList<>();
            labelList.forEach(fi -> labelName.add(fi.getLabelName()));
            question.setLabelName(labelName);
        }

        // 得到用户信息
        Long userId = question.getUserId();
        User user = userMapper.selectById(userId);
        question.setUsername(user.getUsername());
        question.setUserHeadImg(user.getPhoto());
        question.setUserBrief(user.getBrief());
        // 得到问答图片
        question.setPhoto(QuestionPictureEnum.QUESTION_DEFAULT_PIRCUTR.getUrl());
        question.setLikeCount(0);
        question.setCollectCount(0);
        question.setViewCount(0L);
        question.setReplyCount(0);
        questionToSearchFeignService.publishQuestion(JSONObject.toJSONString(question));
        questionToSearchFeignService.userOpusCountAddOne(userId);
    }

    /**
     * 插入文章消息点赞表
     *
     * @param associationId 文章id
     * @param recipientId 接收者id
     * @param senderId 消息发送者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 11:03
     */
    private void insertLikeContentMessage(Long associationId, Long senderId, Long recipientId) {
        MessageLike messageLike = new MessageLike();
        messageLike.setCreateTime(new Date());
        messageLike.setType(MessageEnum.QUESTION_MESSAGE.getCode());
        messageLike.setAssociationId(associationId);
        messageLike.setSenderId(senderId);
        messageLike.setIsComment(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLike.setRecipientId(recipientId);
        messageLikeMapper.insert(messageLike);
    }

    /**
     * 插入回复问答消息表
     *
     * @param questionId 问答id
     * @param senderId 发送者id
     * @param replyContent 发送内容
     * @param recipientId 接收者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:48
     */
    private void insertReplyQuestionMessage(Long questionId, Long senderId, Long recipientId, String replyContent, Long commentId) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.QUESTION_MESSAGE.getCode());
        messageCommentReply.setAssociationId(questionId);
        messageCommentReply.setSendContent(replyContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_REPLY.getCode());
        messageCommentReply.setRecipientId(recipientId);
        messageCommentReply.setCommentId(commentId);
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 插入评论问答消息表表
     *
     * @param questionId 问答id
     * @param senderId 发送者id
     * @param commentContent 发送内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:48
     */
    private void insertCommentQuestionMessage(Long questionId, Long senderId, String commentContent) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.QUESTION_MESSAGE.getCode());
        messageCommentReply.setAssociationId(questionId);
        messageCommentReply.setSendContent(commentContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_COMMENT.getCode());
        Question question = questionMapper.selectById(questionId);
        messageCommentReply.setRecipientId(question.getUserId());
        messageCommentReplyMapper.insert(messageCommentReply);
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

        questionToSearchFeignService.questionReplyCountAdd(questionId);
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

        questionToSearchFeignService.questionLikeCountAddOne(questionId);
        Question question = questionMapper.selectById(questionId);
        questionToSearchFeignService.userLikeCountAddOne(question.getUserId());
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

        questionToSearchFeignService.questionLikeCountSubOne(questionId);
        Question question = questionMapper.selectById(questionId);
        questionToSearchFeignService.userLikeCountSubOne(question.getUserId());
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

        questionToSearchFeignService.questionCollectCountAddOne(questionId);
        Question question = questionMapper.selectById(questionId);
        questionToSearchFeignService.questionCollectCountAddOne(question.getUserId());
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

        questionToSearchFeignService.questionCollectCountSubOne(questionId);
        Question question = questionMapper.selectById(questionId);
        questionToSearchFeignService.userCollectCountSubOne(question.getUserId());
    }

    /**
     * 问答游览数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 14:53
     */
    @Transactional(rollbackFor = Exception.class)
    public void questionViewsAddOne(Long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId);
        updateWrapper.setSql("view_count = view_count + 1");
        questionMapper.update(null, updateWrapper);

        // 更新elasticsearch文章游览数 + 1
        questionToSearchFeignService.questionViewAddOne(questionId);
        Question question = questionMapper.selectById(questionId);
        questionToSearchFeignService.userViewAddOne(question.getUserId());
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

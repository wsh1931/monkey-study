package com.monkey.monkeyarticle.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.MessageCommentReplyMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.mapper.ArticleCommentMapper;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleComment;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.pojo.ArticleLike;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private ArticleMapper articleMapper;
    @Resource
    private ArticleCommentMapper articleCommentMapper;
    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;

    // 文章模块rabbitmq删除队列
    @RabbitListener(queues = RabbitmqQueueName.articleDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 文章模块rabbitmq死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.articleDeleteDlxQueue)
    public void receiverDlxDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq死信删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 文章模块rabbitmq更新队列
    @RabbitListener(queues = RabbitmqQueueName.articleUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq更新队列：event ==> {}", event);
            if (EventConstant.articleLikeCountAddOne.equals(event)) {
                // 文章点赞数 + 1
                Long articleId = data.getLong("articleId");
                articleLikeCountAddOne(articleId);
            } else if (EventConstant.articleLikeCountSubOne.equals(event)) {
                // 文章点赞数 - 1
                Long articleId = data.getLong("articleId");
                articleLikeCountSubOne(articleId);
            } else if (EventConstant.articleViewCountAddOne.equals(event)) {
                // 文章游览数 + 1
                Long articleId = data.getLong("articleId");
                articleViewCountAddOne(articleId);
            } else if (EventConstant.articleCommentCountAddOne.equals(event)) {
                // 文章评论数 + 1
                Long articleId = data.getLong("articleId");
                articleCommentCountAddOne(articleId);
            } else if (EventConstant.articleCommentLikeCountAddOne.equals(event)) {
                // 文章评论点赞数 + 1
                Long articleCommentId = data.getLong("articleCommentId");
                articleCommentLikeCountAddOne(articleCommentId);
            } else if (EventConstant.articleCommentLikeCountSubOne.equals(event)) {
                // 文章评论点赞数 - 1
                Long articleCommentId = data.getLong("articleCommentId");
                articleCommentLikeCountSubOne(articleCommentId);
            } else if (EventConstant.articleCollectCountAddOne.equals(event)) {
                // 文章收藏数 + 1
                Long articleId = data.getLong("articleId");
                articleCollectCountAddOne(articleId);
            } else if (EventConstant.articleCollectCountSubOne.equals(event)) {
                // 文章收藏数 - 1
                Long articleId = data.getLong("articleId");
                articleCollectCountSubOne(articleId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 文章模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.articleUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq死信更新队列：event ==> {}", event);
            if (EventConstant.articleLikeCountAddOne.equals(event)) {
                // 文章点赞数 + 1
                Long articleId = data.getLong("articleId");
                articleLikeCountAddOne(articleId);
            } else if (EventConstant.articleLikeCountSubOne.equals(event)) {
                // 文章点赞数 - 1
                Long articleId = data.getLong("articleId");
                articleLikeCountSubOne(articleId);
            } else if (EventConstant.articleViewCountAddOne.equals(event)) {
                // 文章游览数 + 1
                Long articleId = data.getLong("articleId");
                articleViewCountAddOne(articleId);
            } else if (EventConstant.articleCommentCountAddOne.equals(event)) {
                // 文章评论数 + 1
                Long articleId = data.getLong("articleId");
                articleCommentCountAddOne(articleId);
            } else if (EventConstant.articleCommentLikeCountAddOne.equals(event)) {
                // 文章评论点赞数 + 1
                Long articleCommentId = data.getLong("articleCommentId");
                articleCommentLikeCountAddOne(articleCommentId);
            } else if (EventConstant.articleCommentLikeCountSubOne.equals(event)) {
                // 文章评论点赞数 - 1
                Long articleCommentId = data.getLong("articleCommentId");
                articleCommentLikeCountSubOne(articleCommentId);
            } else if (EventConstant.articleCollectCountAddOne.equals(event)) {
                // 文章收藏数 + 1
                Long articleId = data.getLong("articleId");
                articleCollectCountAddOne(articleId);
            } else if (EventConstant.articleCollectCountSubOne.equals(event)) {
                // 文章收藏数 - 1
                Long articleId = data.getLong("articleId");
                articleCollectCountSubOne(articleId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 文章模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.articleInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq插入队列：event ==> {}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                String content = data.getString("content");
                String profile = data.getString("profile");
                String photo = data.getString("photo");
                String title = data.getString("title");
                String labelId = data.getString("labelId");
                Long userId = data.getLong("userId");
                publishArticle(content, profile, photo, title, labelId, userId);
            } else if (EventConstant.commentInsertArticleMessage.equals(event)) {
                log.info("评论插入文章消息表");
                Long articleId = data.getLong("articleId");
                Long senderId = data.getLong("senderId");
                String content = data.getString("content");
                this.commentInsertArticleMessage(articleId, senderId, content);
            } else if (EventConstant.replyInsertArticleMessage.equals(event)) {
                log.info("评论回复插入文章消息表");
                Long articleId = data.getLong("articleId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                this.replyInsertArticleMessage(articleId, senderId, recipientId, replyContent);
            }

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 文章模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.articleInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("文章模块rabbitmq死信插入队列：event ==> {}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                String content = data.getString("content");
                String profile = data.getString("profile");
                String photo = data.getString("photo");
                String title = data.getString("title");
                String labelId = data.getString("labelId");
                Long userId = data.getLong("userId");
                publishArticle(content, profile, photo, title, labelId, userId);
            } else if (EventConstant.commentInsertArticleMessage.equals(event)) {
                log.info("评论插入文章消息表");
                Long articleId = data.getLong("articleId");
                Long senderId = data.getLong("senderId");
                String content = data.getString("content");
                this.commentInsertArticleMessage(articleId, senderId, content);
            } else if (EventConstant.replyInsertArticleMessage.equals(event)) {
                log.info("评论回复插入文章消息表");
                Long articleId = data.getLong("articleId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                this.replyInsertArticleMessage(articleId, senderId, recipientId, replyContent);
            }

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 评论回复插入文章消息表
     *
     * @param articleId 文章id
     * @param senderId 发送者id
     * @param replyContent 发送内容
     * @param recipientId 接收者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:01
     */
    private void replyInsertArticleMessage(Long articleId, Long senderId, Long recipientId, String replyContent) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.ARTICLE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(articleId);
        messageCommentReply.setSendContent(replyContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_REPLY.getCode());
        messageCommentReply.setRecipientId(recipientId);
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 评论插入文章消息表
     *
     * @param articleId 文章id
     * @param senderId 发送者id
     * @param content 发送内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 15:47
     */
    private void commentInsertArticleMessage(Long articleId, Long senderId, String content) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.ARTICLE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(articleId);
        messageCommentReply.setSendContent(content);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_COMMENT.getCode());
        Article article = articleMapper.selectById(articleId);
        messageCommentReply.setRecipientId(article.getUserId());
        messageCommentReplyMapper.insert(messageCommentReply);
    }
    /**
     * 发布社区文章
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 13:49
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(String content, String profile, String photo, String title, String labelId, Long userId) {
        labelId = labelId.substring(1, labelId.length() - 1);
        String[] labelIdList = labelId.split(",");

        Article article = new Article();
        article.setContent(content);
        article.setUserId(userId);
        article.setTitle(title);
        article.setCreateTime(new Date());
        article.setProfile(profile);
        article.setPhoto(photo);
        article.setUpdateTime(new Date());
        articleMapper.insert(article);


        for (String label : labelIdList) {
            long labelid = Long.parseLong(label);
            ArticleLabel articleLabel = new ArticleLabel();
            articleLabel.setArticleId(article.getId());
            articleLabel.setLabelId(labelid);
            articleLabelMapper.insert(articleLabel);
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
    /**
     * 文章收藏数 - 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 14:01
     */
    private void articleCollectCountSubOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("collect_count = collect_count - 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 文章收藏数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 13:59
     */
    private void articleCollectCountAddOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("collect_count = collect_count + 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 文章评论点赞数 - 1
     *
     * @param articleCommentId 文章评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:25
     */
    private void articleCommentLikeCountSubOne(Long articleCommentId) {
        UpdateWrapper<ArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleCommentId);
        updateWrapper.setSql("like_sum = like_sum - 1");
        articleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 文章评论点赞数 + 1
     *
     * @param articleCommentId 文章评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:25
     */
    private void articleCommentLikeCountAddOne(Long articleCommentId) {
        UpdateWrapper<ArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleCommentId);
        updateWrapper.setSql("like_sum = like_sum + 1");
        articleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 文章评论数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:18
     */
    private void articleCommentCountAddOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("comment_count = comment_count + 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 文章游览数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:10
     */
    private void articleViewCountAddOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("visit = visit + 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 文章点赞数 - 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:01
     */
    private void articleLikeCountSubOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("like_count = like_count - 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 文章点赞数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 9:01
     */
    private void articleLikeCountAddOne(Long articleId) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId);
        updateWrapper.setSql("like_count = like_count + 1");
        articleMapper.update(null, updateWrapper);
    }
}

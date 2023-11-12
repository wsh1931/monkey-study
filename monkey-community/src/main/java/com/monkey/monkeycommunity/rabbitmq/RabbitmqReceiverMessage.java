package com.monkey.monkeycommunity.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.mapper.MessageCommentReplyMapper;
import com.monkey.monkeyUtils.mapper.MessageLikeMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.pojo.MessageLike;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeycommunity.constant.CommunityChannelEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.feign.CommunityToSearchFeign;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;
import com.monkey.monkeycommunity.pojo.vo.CommunityVo;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
    private CommunityArticleVoteMapper communityArticleVoteMapper;
    @Resource
    private CommunityArticleVoteItemMapper communityArticleVoteItemMapper;
    @Resource
    private CommunityArticleTaskMapper communityArticleTaskMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityArticleScoreMapper communityArticleScoreMapper;
    @Resource
    private CommunityArticleTaskReplyMapper communityArticleTaskReplyMapper;
    @Resource
    private CommunityArticleCommentMapper communityArticleCommentMapper;
    @Resource
    private CommunityArticleCommentLikeMapper communityArticleCommentLikeMapper;
    @Resource
    private CommunityArticleLikeMapper communityArticleLikeMapper;
    @Resource
    private CommunityUserApplicationMapper communityUserApplicationMapper;
    @Resource
    private CommunityUserInviteMapper communityUserInviteMapper;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityUserManageMapper communityUserManageMapper;
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private CommunityToSearchFeign communityToSearchFeign;

    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;

    @Resource
    private CommunityAttributeMapper communityAttributeMapper;
    // 社区直连队列
    @RabbitListener(queues = RabbitmqQueueName.communityDirectQueue)
    public void receiverDirectQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody());
            String event = data.getString("event");
            log.info("社区直连队列：event ==> {}", event);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 社区死信直连队列
    @RabbitListener(queues = RabbitmqQueueName.communityDirectDlxQueue)
    public void receiverDlxDirectQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody());
            String event = data.getString("event");
            log.info("社区死信直连队列：event ==> {}", event);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 社区直连死信队列

    // 正常更新队列
    @RabbitListener(queues = RabbitmqQueueName.communityUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            String event = data.getString("event");
            log.info("正常更新队列时间 ==> event : {}", event);
            if (EventConstant.communityArticleCountAddOne.equals(event)) {
                // 社区文章数 + 1;
                this.communityArticleCountAddOne(data);
            } else if (EventConstant.communityMemberCountAddOne.equals(event)) {
                // 社区成员数 + 1
                Long communityId = data.getLong("communityId");
                this.communityMemberCountAddone(communityId);
            } else if (EventConstant.getCommunityMemberCountSubOne.equals(event)) {
                // 社区成员数 - 1
                Long communityId = data.getLong("communityId");
                this.communityMemberCountSubOne(communityId);
            } else if (EventConstant.communityArticleCountSubOne.equals(event)) {
                Long communityId = data.getLong("communityId");
                log.info("社区文章数减一：communityId：{}", communityId);
                this.communityArticleCountSubOne(communityId);
            } else if (EventConstant.communityArticleVotePeopleAddOne.equals(event)) {
                // 社区文章投票人数 + 1；
                Long communityArticleVoteId = data.getLong("communityArticleVoteId");
                this.communityArticleVotePeopleAddOne(communityArticleVoteId);
            } else if (EventConstant.communityArticleViewCountAddOne.equals(event)) {
                // 文章社区游览数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleViewCountAddOne(communityArticleId);
            } else if (EventConstant.communityArticleTaskReplyCountAddOne.equals(event)) {
                // 社区文章任务回复数 + 1
                Long communityArticleTaskId = data.getLong("communityArticleTaskId");
                this.communityArticleTaskReplyCountAddOne(communityArticleTaskId);
            } else if (EventConstant.curationComment.equals(event)) {
                // 精选评论
                Long commentId = data.getLong("commentId");
                this.curationComment(commentId);
            } else if (EventConstant.cancelCurationComment.equals(event)) {
                // 取消精选评论
                Long commentId = data.getLong("commentId");
                this.cancelCurationComment(commentId);
            } else if (EventConstant.topComment.equals(event)) {
                // 置顶评论
                Long commentId = data.getLong("commentId");
                this.topComment(commentId);
            } else if (EventConstant.cancelTopComment.equals(event)) {
                // 取消置顶评论
                Long commentId = data.getLong("commentId");
                this.cancelTopComment(commentId);
            } else if (EventConstant.communityArticleCommentAddOne.equals(event)) {
                // 社区文章评论数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCommentAddOne(communityArticleId);
            } else if (EventConstant.communityArticleCollectCountAddOne.equals(event)) {
                // 社区文章收藏数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCollectCountAddOne(communityArticleId);
            }  else if (EventConstant.communityArticleCollectCountSubOne.equals(event)) {
                // 社区文章收藏数 - 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCollectCountSubOne(communityArticleId);
            } else if (EventConstant.communityArticleExcellent.equals(event)) {
                // 社区文章精选
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleExcellent(communityArticleId);
            } else if (EventConstant.cancelCommunityArticleExcellent.equals(event)) {
                // 社区文章取消精选
                Long communityArticleId = data.getLong("communityArticleId");
                this.cancelCommunityArticleExcellent(communityArticleId);
            } else if (EventConstant.communityArticleTop.equals(event)) {
                // 社区文章置顶
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleTop(communityArticleId);
            } else if (EventConstant.cancelCommunityArticleTop.equals(event)) {
                // 社区文章取消置顶
                Long communityArticleId = data.getLong("communityArticleId");
                this.cancelCommunityArticleTop(communityArticleId);
            } else if (EventConstant.updateCommunityArticleChannel.equals(event)) {
                // 修改社区文章频道
                Long communityArticleId = data.getLong("communityArticleId");
                Long channelId = data.getLong("channelId");
                this.updateCommunityArticleChannel(channelId, communityArticleId);
            } else if (EventConstant.updateUserConnectRole.equals(event)) {
                log.info("将有该角色的用户更新为社区员工");
                Long roleId = data.getLong("roleId");
                Long communityId = data.getLong("communityId");
                this.updateUserConnectRole(roleId, communityId);
            } else if (EventConstant.updateSupportShow.equals(event)) {
                log.info("更新是否支持前端展示");
                Long channelId = data.getLong("channelId");
                Integer supportShow = data.getInteger("supportShow");
                this.updateSupportShow(channelId, supportShow);
            } else if (EventConstant.updateSupportUserPublish.equals(event)) {
                log.info("更新是否支持用户发表文章");
                Long channelId = data.getLong("channelId");
                Integer supportUserPublish = data.getInteger("supportUserPublish");
                this.updateSupportUserPublish(channelId, supportUserPublish);
            } else if (EventConstant.updateSupportManageModify.equals(event)) {
                log.info("更新是否支持管理员修改");
                Long channelId = data.getLong("channelId");
                Integer supportManageModify = data.getInteger("supportManageModify");
                this.updateSupportManageModify(channelId, supportManageModify);
            } else if (EventConstant.updateCommunityInformation.equals(event)) {
                log.info("更新社区信息");
                CommunityVo communityVo = JSONObject.parseObject(data.getString("communityVoStr"), CommunityVo.class);
                this.updateCommunityInformation(communityVo);
            } else if (EventConstant.updateCommunityNotice.equals(event)) {
                log.info("更新社区通知");
                Long communityId = data.getLong("communityId");
                String communityNotice = data.getString("communityNotice");
                this.updateCommunityNotice(communityId, communityNotice);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.communityUpdateDlxQueue)
    public void receiverUpdateDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            String event = data.getString("event");
            log.info("死信更新队列时间 ==> event : {}", event);
            if (EventConstant.communityArticleCountAddOne.equals(event)) {
                // 社区文章数 + 1;
                this.communityArticleCountAddOne(data);
            } else if (EventConstant.communityMemberCountAddOne.equals(event)) {
                // 社区成员数 + 1
                Long communityId = data.getLong("communityId");
                this.communityMemberCountAddone(communityId);
            } else if (EventConstant.getCommunityMemberCountSubOne.equals(event)) {
                // 社区成员数 - 1
                Long communityId = data.getLong("communityId");
                this.communityMemberCountSubOne(communityId);
            } else if (EventConstant.communityArticleCountSubOne.equals(event)) {
                Long communityId = data.getLong("communityId");
                log.info("社区文章数减一：communityId：{}", communityId);
                this.communityArticleCountSubOne(communityId);
            } else if (EventConstant.communityArticleVotePeopleAddOne.equals(event)) {
                // 社区文章投票人数 + 1；
                Long communityArticleVoteId = data.getLong("communityArticleVoteId");
                this.communityArticleVotePeopleAddOne(communityArticleVoteId);
            } else if (EventConstant.communityArticleViewCountAddOne.equals(event)) {
                // 文章社区游览数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleViewCountAddOne(communityArticleId);
            } else if (EventConstant.communityArticleTaskReplyCountAddOne.equals(event)) {
                // 社区文章任务回复数 + 1
                Long communityArticleTaskId = data.getLong("communityArticleTaskId");
                this.communityArticleTaskReplyCountAddOne(communityArticleTaskId);
            } else if (EventConstant.curationComment.equals(event)) {
                // 精选评论
                Long commentId = data.getLong("commentId");
                this.curationComment(commentId);
            } else if (EventConstant.cancelCurationComment.equals(event)) {
                // 取消精选评论
                Long commentId = data.getLong("commentId");
                this.cancelCurationComment(commentId);
            } else if (EventConstant.topComment.equals(event)) {
                // 置顶评论
                Long commentId = data.getLong("commentId");
                this.topComment(commentId);
            } else if (EventConstant.cancelTopComment.equals(event)) {
                // 取消置顶评论
                Long commentId = data.getLong("commentId");
                this.cancelTopComment(commentId);
            } else if (EventConstant.communityArticleCommentAddOne.equals(event)) {
                // 社区文章评论数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCommentAddOne(communityArticleId);
            } else if (EventConstant.communityArticleCollectCountAddOne.equals(event)) {
                // 社区文章收藏数 + 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCollectCountAddOne(communityArticleId);
            }  else if (EventConstant.communityArticleCollectCountSubOne.equals(event)) {
                // 社区文章收藏数 - 1
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCollectCountSubOne(communityArticleId);
            } else if (EventConstant.communityArticleExcellent.equals(event)) {
                // 社区文章精选
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleExcellent(communityArticleId);
            } else if (EventConstant.cancelCommunityArticleExcellent.equals(event)) {
                // 社区文章取消精选
                Long communityArticleId = data.getLong("communityArticleId");
                this.cancelCommunityArticleExcellent(communityArticleId);
            } else if (EventConstant.communityArticleTop.equals(event)) {
                // 社区文章置顶
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleTop(communityArticleId);
            } else if (EventConstant.cancelCommunityArticleTop.equals(event)) {
                // 社区文章取消置顶
                Long communityArticleId = data.getLong("communityArticleId");
                this.cancelCommunityArticleTop(communityArticleId);
            } else if (EventConstant.updateCommunityArticleChannel.equals(event)) {
                // 修改社区文章频道
                Long communityArticleId = data.getLong("communityArticleId");
                Long channelId = data.getLong("channelId");
                this.updateCommunityArticleChannel(channelId, communityArticleId);
            } else if (EventConstant.updateUserConnectRole.equals(event)) {
                log.info("将有该角色的用户更新为社区员工");
                Long roleId = data.getLong("roleId");
                Long communityId = data.getLong("communityId");
                this.updateUserConnectRole(roleId, communityId);
            } else if (EventConstant.updateSupportShow.equals(event)) {
                log.info("更新是否支持前端展示");
                Long channelId = data.getLong("channelId");
                Integer supportShow = data.getInteger("supportShow");
                this.updateSupportShow(channelId, supportShow);
            } else if (EventConstant.updateSupportUserPublish.equals(event)) {
                log.info("更新是否支持用户发表文章");
                Long channelId = data.getLong("channelId");
                Integer supportUserPublish = data.getInteger("supportUserPublish");
                this.updateSupportUserPublish(channelId, supportUserPublish);
            } else if (EventConstant.updateSupportManageModify.equals(event)) {
                log.info("更新是否支持管理员修改");
                Long channelId = data.getLong("channelId");
                Integer supportManageModify = data.getInteger("supportManageModify");
                this.updateSupportManageModify(channelId, supportManageModify);
            } else if (EventConstant.updateCommunityInformation.equals(event)) {
                log.info("更新社区信息");
                CommunityVo communityVo = JSONObject.parseObject(data.getString("communityVoStr"), CommunityVo.class);
                this.updateCommunityInformation(communityVo);
            } else if (EventConstant.updateCommunityNotice.equals(event)) {
                log.info("更新社区通知");
                Long communityId = data.getLong("communityId");
                String communityNotice = data.getString("communityNotice");
                this.updateCommunityNotice(communityId, communityNotice);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 正常插入队列
    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = RabbitmqQueueName.communityInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            String event = data.getString("event");
            log.info("社区模块正常插入队列 ==》 event：{}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                CommunityArticleVo communityArticleVo = JSONObject.parseObject(data.getString("communityArticleVo"), CommunityArticleVo.class);
                Long communityId = data.getLong("communityId");
                Long userId = data.getLong("userId");
                this.publishArticle(userId, communityId, communityArticleVo);
            } else if (EventConstant.communityArticleScore.equals(event)) {
                // 社区文章评分
                Long userId = data.getLong("userId");
                Integer articleScore = data.getInteger("articleScore");
                Long communityArticleId = data.getLong("communityArticleId");
                Long communityId = data.getLong("communityId");
                this.communityArticleScore(userId, articleScore, communityArticleId, communityId);
            } else if (EventConstant.commentLike.equals(event)) {
                // 评论点赞
                Long userId = data.getLong("userId");
                Long commentId = data.getLong("commentId");
                this.commentLike(userId, commentId);
            } else if (EventConstant.cancelCommentLike.equals(event)) {
                // 取消评论点赞
                Long userId = data.getLong("userId");
                Long commentId = data.getLong("commentId");
                this.cancelCommentLike(userId, commentId);
            } else if (EventConstant.communityArticleLike.equals(event)) {
                // 社区文章点赞
                Long userId = data.getLong("userId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleLike(userId, communityArticleId);
            }  else if (EventConstant.communityArticleCancelLike.equals(event)) {
                // 社区文章取消点赞
                Long userId = data.getLong("userId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCancelLike(userId, communityArticleId);
            } else if (EventConstant.addCommunityUserApplication.equals(event)) {
                // 加入社区用户申请表
                Long userId = data.getLong("userId");
                Long communityId = data.getLong("communityId");
                this.addCommunityUserApplication(userId, communityId);
            } else if (EventConstant.inviteUserEnterCommunity.equals(event)) {
                log.info("邀请用户加入社区");
                Long communityId = data.getLong("communityId");
                Long inviteUserId = data.getLong("inviteUserId");
                Long inviteRoleId = data.getLong("inviteRoleId");
                Long nowUserId = data.getLong("nowUserId");
                this.inviteUserEnterCommunity(communityId, nowUserId, inviteUserId, inviteRoleId);
            } else if (EventConstant.createCommunity.equals(event)) {
                log.info("创建社区");
                String communityStr = data.getString("community");
                Long userId = data.getLong("userId");
                Community community = JSONObject.parseObject(communityStr, Community.class);
                this.createCommunity(community, userId);
            } else if (EventConstant.addCommunityRole.equals(event)) {
                log.info("添加社区角色");
                CommunityRole communityRole = JSONObject.parseObject(data.getString("communityRole"), CommunityRole.class);
                Long communityId = data.getLong("communityId");
                this.addCommunityRole(communityRole, communityId);
            } else if (EventConstant.commentInsertArticleMessage.equals(event)) {
                log.info("评论插入文章消息表");
                Long communityArticleId = data.getLong("communityArticleId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.commentInsertArticleMessage(communityArticleId, senderId, commentContent);
            } else if (EventConstant.replyInsertArticleMessage.equals(event)) {
                log.info("评论回复插入文章消息表");
                Long communityArticleId = data.getLong("communityArticleId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.replyInsertArticleMessage(communityArticleId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertCommunityArticleLikeContentMessage.equals(event)) {
                log.info("插入社区文章消息点赞表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                this.insertLikeContentMessage(associationId, senderId, recipientId);
            } else if (EventConstant.insertCommunityArticleLikeCommentMessage.equals(event)) {
                log.info("插入社区文章评论消息点赞内容表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.insertLikeCommentMessage(associationId, senderId, recipientId, commentId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.communityInsertDlxQueue)
    public void receiverInsertDlxQueue(Message message) {
        String event = "";
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body);
            event = data.getString("event");
            log.info("死信插入队列 ==》 event：{}", event);
            if (EventConstant.publishArticle.equals(event)) {
                // 发布文章
                CommunityArticleVo communityArticleVo = JSONObject.parseObject(data.getString("communityArticleVo"), CommunityArticleVo.class);
                Long communityId = data.getLong("communityId");
                Long userId = data.getLong("userId");
                this.publishArticle(userId, communityId, communityArticleVo);
            } else if (EventConstant.communityArticleScore.equals(event)) {
                // 社区文章评分
                Long userId = data.getLong("userId");
                Integer articleScore = data.getInteger("articleScore");
                Long communityArticleId = data.getLong("communityArticleId");
                Long communityId = data.getLong("communityId");
                this.communityArticleScore(userId, articleScore, communityArticleId, communityId);
            } else if (EventConstant.commentLike.equals(event)) {
                // 评论点赞
                Long userId = data.getLong("userId");
                Long commentId = data.getLong("commentId");
                this.commentLike(userId, commentId);
            } else if (EventConstant.cancelCommentLike.equals(event)) {
                // 取消评论点赞
                Long userId = data.getLong("userId");
                Long commentId = data.getLong("commentId");
                this.cancelCommentLike(userId, commentId);
            } else if (EventConstant.communityArticleLike.equals(event)) {
                // 社区文章点赞
                Long userId = data.getLong("userId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleLike(userId, communityArticleId);
            }  else if (EventConstant.communityArticleCancelLike.equals(event)) {
                // 社区文章取消点赞
                Long userId = data.getLong("userId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.communityArticleCancelLike(userId, communityArticleId);
            } else if (EventConstant.addCommunityUserApplication.equals(event)) {
                // 加入社区用户申请表
                Long userId = data.getLong("userId");
                Long communityId = data.getLong("communityId");
                this.addCommunityUserApplication(userId, communityId);
            } else if (EventConstant.inviteUserEnterCommunity.equals(event)) {
                log.info("邀请用户加入社区");
                Long communityId = data.getLong("communityId");
                Long inviteUserId = data.getLong("inviteUserId");
                Long inviteRoleId = data.getLong("inviteRoleId");
                Long nowUserId = data.getLong("nowUserId");
                this.inviteUserEnterCommunity(communityId, nowUserId, inviteUserId, inviteRoleId);
            } else if (EventConstant.createCommunity.equals(event)) {
                log.info("创建社区");
                String communityStr = data.getString("community");
                Long userId = data.getLong("userId");
                Community community = JSONObject.parseObject(communityStr, Community.class);
                this.createCommunity(community, userId);
            } else if (EventConstant.addCommunityRole.equals(event)) {
                log.info("添加社区角色");
                CommunityRole communityRole = JSONObject.parseObject(data.getString("communityRole"), CommunityRole.class);
                Long communityId = data.getLong("communityId");
                this.addCommunityRole(communityRole, communityId);
            } else if (EventConstant.commentInsertArticleMessage.equals(event)) {
                log.info("评论插入文章消息表");
                Long communityArticleId = data.getLong("communityArticleId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.commentInsertArticleMessage(communityArticleId, senderId, commentContent);
            } else if (EventConstant.replyInsertArticleMessage.equals(event)) {
                log.info("评论回复插入文章消息表");
                Long communityArticleId = data.getLong("communityArticleId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.replyInsertArticleMessage(communityArticleId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertCommunityArticleLikeContentMessage.equals(event)) {
                log.info("插入社区文章消息点赞表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                this.insertLikeContentMessage(associationId, senderId, recipientId);
            } else if (EventConstant.insertCommunityArticleLikeCommentMessage.equals(event)) {
                log.info("插入社区文章评论消息点赞内容表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.insertLikeCommentMessage(associationId, senderId, recipientId, commentId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 正常删除队列
    @RabbitListener(queues = RabbitmqQueueName.communityDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody());
            String event = data.getString("event");
            log.info("正常删除队列: event ==> {}", event);
            if (event.equals(EventConstant.deleteCommunityArticle)) {
                // 删除社区文章
                Long communityArticleId = data.getLong("communityArticleId");
                Long communityId = data.getLong("communityId");
                this.deleteCommunityArticle(communityId, communityArticleId);
            } else if (EventConstant.deleteCommunityArticleTaskReply.equals(event)) {
                // 删除任务历史记录
                Long communityArticleTaskReplyId = data.getLong("communityArticleTaskReplyId");
                Long communityArticleTaskId = data.getLong("communityArticleTaskId");
                this.deleteCommunityArticleTaskReply(communityArticleTaskId, communityArticleTaskReplyId);
            } else if (EventConstant.deleteComment.equals(event)) {
                // 删除社区文章评论
                Long commentId = data.getLong("commentId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.deleteComment(commentId, communityArticleId);
            } else if (EventConstant.deleteAllSuccessApplicationRecords.equals(event)) {
                log.info("删除全部已通过用户申请记录");
                Long communityId = data.getLong("communityId");
                this.deleteAllSuccessApplicationRecords(communityId);
            } else if (EventConstant.deleteAllRefuseApplicationRecords.equals(event)) {
                log.info("删除全部拒绝用户申请记录");
                Long communityId = data.getLong("communityId");
                this.deleteAllRefuseApplicationRecords(communityId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            this.addToRabbitmqErrorLog(message, e);
            // 注意不能throw不然队列会一直接收消息
        }
    }

    // 死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.communityDeleteDlxQueue)
    public void reveiverDeleteDlxQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody());
            String event = data.getString("event");
            log.info("死信删除队列: event ==> {}", event);
            if (event.equals(EventConstant.deleteCommunityArticle)) {
                // 删除社区文章
                Long communityArticleId = data.getLong("communityArticleId");
                Long communityId = data.getLong("communityId");
                this.deleteCommunityArticle(communityId, communityArticleId);
            } else if (EventConstant.deleteCommunityArticleTaskReply.equals(event)) {
                // 删除任务历史记录
                Long communityArticleTaskReplyId = data.getLong("communityArticleTaskReplyId");
                Long communityArticleTaskId = data.getLong("communityArticleTaskId");
                this.deleteCommunityArticleTaskReply(communityArticleTaskId, communityArticleTaskReplyId);
            } else if (EventConstant.deleteComment.equals(event)) {
                // 删除社区文章评论
                Long commentId = data.getLong("commentId");
                Long communityArticleId = data.getLong("communityArticleId");
                this.deleteComment(commentId, communityArticleId);
            } else if (EventConstant.deleteAllSuccessApplicationRecords.equals(event)) {
                log.info("删除全部已通过用户申请记录");
                Long communityId = data.getLong("communityId");
                this.deleteAllSuccessApplicationRecords(communityId);
            } else if (EventConstant.deleteAllRefuseApplicationRecords.equals(event)) {
                log.info("删除全部拒绝用户申请记录");
                Long communityId = data.getLong("communityId");
                this.deleteAllRefuseApplicationRecords(communityId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            this.addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 插入文章评论消息点赞内容表
     *
     * @param associationId 社区文章id
     * @param recipientId 接收者id
     * @param senderId 消息发送者id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 11:07
     */
    private void insertLikeCommentMessage(Long associationId, Long senderId, Long recipientId, Long commentId) {
        MessageLike messageLike = new MessageLike();
        messageLike.setCreateTime(new Date());
        messageLike.setType(MessageEnum.COMMUNITY_ARTICLE_MESSAGE.getCode());
        messageLike.setAssociationId(associationId);
        messageLike.setSenderId(senderId);
        messageLike.setIsComment(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode());
        messageLike.setRecipientId(recipientId);
        messageLike.setCommentId(commentId);
        messageLikeMapper.insert(messageLike);
    }

    /**
     * 插入文章消息点赞表
     *
     * @param associationId 社区文章id
     * @param recipientId 接收者id
     * @param senderId 消息发送者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 11:03
     */
    private void insertLikeContentMessage(Long associationId, Long senderId, Long recipientId) {
        MessageLike messageLike = new MessageLike();
        messageLike.setCreateTime(new Date());
        messageLike.setType(MessageEnum.COMMUNITY_ARTICLE_MESSAGE.getCode());
        messageLike.setAssociationId(associationId);
        messageLike.setSenderId(senderId);
        messageLike.setIsComment(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLike.setRecipientId(recipientId);
        messageLikeMapper.insert(messageLike);
    }

    /**
     * 评论回复插入文章消息表
     *
     * @param communityArticleId 文章id
     * @param senderId 发送者id
     * @param replyContent 发送内容
     * @param recipientId 接收者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:14
     */
    private void replyInsertArticleMessage(Long communityArticleId, Long senderId, Long recipientId, String replyContent, Long commentId) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.COMMUNITY_ARTICLE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(communityArticleId);
        messageCommentReply.setSendContent(replyContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_REPLY.getCode());
        messageCommentReply.setRecipientId(recipientId);
        messageCommentReply.setCommentId(commentId);
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 评论插入文章消息表
     *
     * @param communityArticleId 文章id
     * @param senderId 发送者id
     * @param commentContent 发送内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:14
     */
    private void commentInsertArticleMessage(Long communityArticleId, Long senderId, String commentContent) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.COMMUNITY_ARTICLE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(communityArticleId);
        messageCommentReply.setSendContent(commentContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_COMMENT.getCode());
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        messageCommentReply.setRecipientId(communityArticle.getUserId());
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 更新社区通知
     *
     * @param communityNotice 社区通知
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 16:31
     */
    private void updateCommunityNotice(Long communityId, String communityNotice) {
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.set("notice", communityNotice);
        communityMapper.update(null, updateWrapper);
    }

    /**
     * 更新社区信息
     *
     * @param communityVo 社区Vo
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 16:05
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCommunityInformation(CommunityVo communityVo) {
        Long communityId = communityVo.getId();
        Community community = new Community();
        Date updateTime = new Date();
        community.setId(communityVo.getId());
        community.setUpdateTime(updateTime);
        community.setDescription(communityVo.getDescription());
        community.setName(communityVo.getName());
        community.setPhoto(communityVo.getPhoto());
        community.setClassificationId(communityVo.getClassificationId());
        community.setIsComment(communityVo.getIsComment());
        community.setAttributeLabelId(community.getAttributeLabelId());
        community.setEnterWay(community.getEnterWay());
        communityMapper.updateById(community);


        // 更新社区内容标签
        // 删除之前的社区标签关联
        QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
        communityLabelConnectQueryWrapper.eq("community_id", communityId);
        communityLabelConnectMapper.delete(communityLabelConnectQueryWrapper);

        // 插入新的社区标签关联表
        List<CommunityClassificationLabel> communityClassificationLabelList = communityVo.getCommunityClassificationLabelList();
        for (CommunityClassificationLabel communityClassificationLabel : communityClassificationLabelList) {
            CommunityLabelConnect communityLabelConnect = new CommunityLabelConnect();
            communityLabelConnect.setCommunityId(communityId);
            communityLabelConnect.setCommunityClassificationLabelId(communityClassificationLabel.getId());
            communityLabelConnect.setCreateTime(updateTime);
            communityLabelConnectMapper.insert(communityLabelConnect);
        }
    }


    /**
     * 更新是否支持管理员修改
     *
     * @param channelId 频道id
     * @param supportManageModify 需要更新的字段/数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 21:45
     */
    private void updateSupportManageModify(Long channelId, Integer supportManageModify) {
        UpdateWrapper<CommunityChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", channelId);
        updateWrapper.set("support_manage_modify", supportManageModify);
        updateWrapper.set("update_time", new Date());
        communityChannelMapper.update(null, updateWrapper);
    }
    /**
     * 更新是否支持用户发表文章
     *
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 21:45
     */
    private void updateSupportUserPublish(Long channelId, Integer supportUserPublish) {
        UpdateWrapper<CommunityChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", channelId);
        updateWrapper.set("support_user_publish", supportUserPublish);
        updateWrapper.set("update_time", new Date());
        communityChannelMapper.update(null, updateWrapper);
    }

    /**
     * 更新是否支持前端展示
     *
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 21:45
     */
    private void updateSupportShow(Long channelId, Integer supportShow) {
        UpdateWrapper<CommunityChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", channelId);
        updateWrapper.set("support_show", supportShow);
        updateWrapper.set("update_time", new Date());
        communityChannelMapper.update(null, updateWrapper);
    }

    /**
     * 添加社区角色
     *
     * @param communityRole 社区角色实体类
     * @param communityId
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/3 10:08
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCommunityRole(CommunityRole communityRole, Long communityId) {
        Date date = new Date();
        // 插入社区角色表
        CommunityRole role = new CommunityRole();
        role.setCommunityId(communityRole.getCommunityId());
        role.setRoleName(communityRole.getRoleName());
        role.setPromotionCondition(communityRole.getPromotionCondition());
        role.setDownName(communityRole.getDownName());
        role.setRelatedBenefit(communityRole.getRelatedBenefit());
        role.setCreateTime(date);
        role.setUpdateTime(date);
        communityRoleMapper.insert(role);

        Long roleId = role.getId();

        // 添加社区角色关联表
        CommunityRoleConnect communityRoleConnect = new CommunityRoleConnect();
        communityRoleConnect.setCommunityId(communityId);
        communityRoleConnect.setRoleId(roleId);
        communityRoleConnect.setCreateTime(date);
        communityRoleConnectMapper.insert(communityRoleConnect);

    }

    /**
     * 将有该角色的用户更新为社区员工
     *
     * @param roleId 角色id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 17:28
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserConnectRole(Long roleId, Long communityId) {
        // 查找该社区的所有员工, 找到该社区社区员工的id
        QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
        communityRoleQueryWrapper.eq("community_id", communityId);
        communityRoleQueryWrapper.eq("role_name", CommunityRoleEnum.MEMBER.getMsg());
        communityRoleQueryWrapper.select("id");
        CommunityRole communityRole = communityRoleMapper.selectOne(communityRoleQueryWrapper);

        // 替换被删除的角色id为社区员工
        UpdateWrapper<CommunityUserRoleConnect> communityUserRoleConnectUpdateWrapper = new UpdateWrapper<>();
        communityUserRoleConnectUpdateWrapper.eq("community_id", communityId);
        communityUserRoleConnectUpdateWrapper.eq("role_id", roleId);
        communityUserRoleConnectUpdateWrapper.set("role_id", communityRole.getId());
        communityUserRoleConnectMapper.update(null, communityUserRoleConnectUpdateWrapper);

        // 从社区角色关系表中删除此元素
        QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("role_id", roleId);
        communityRoleConnectMapper.delete(communityRoleConnectQueryWrapper);

        // 删除社区角色表
        communityRoleMapper.deleteById(roleId);
    }


    /**
     * 删除全部拒绝用户申请记录
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 15:19
     */
    private void deleteAllRefuseApplicationRecords(Long communityId) {
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.eq("community_id", communityId);
        communityUserApplicationQueryWrapper.eq("status", CommunityEnum.ALREADY_REFUSE.getCode());
        communityUserApplicationMapper.delete(communityUserApplicationQueryWrapper);
    }

    /**
     * 删除全部已通过用户申请记录
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 15:19
     */
    private void deleteAllSuccessApplicationRecords(Long communityId) {
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.eq("community_id", communityId);
        communityUserApplicationQueryWrapper.eq("status", CommunityEnum.ALREADY_APPROVAL.getCode());
        communityUserApplicationMapper.delete(communityUserApplicationQueryWrapper);
    }

    /**
     * 创建社区
     *
     * @param community 社区实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/1 10:54
     */
    @Transactional(rollbackFor = Exception.class)
    public void createCommunity(Community community, Long userId) {

        Date createTime = new Date();
        Date time = createTime;
        community.setCreateTime(time);
        community.setUpdateTime(time);
        community.setUserId(userId);
        community.setMemberCount(1L);
        communityMapper.insert(community);


        // 添加社区标签表
        List<CommunityClassificationLabel> communityClassificationLabelList = community.getCommunityClassificationLabelList();
        Long communityId = community.getId();
        for (CommunityClassificationLabel communityClassificationLabel : communityClassificationLabelList) {
            Long communityClassificationLabelId = communityClassificationLabel.getId();
            CommunityLabelConnect communityLabelConnect = new CommunityLabelConnect();
            communityLabelConnect.setCommunityId(communityId);
            communityLabelConnect.setCommunityClassificationLabelId(communityClassificationLabelId);
            communityLabelConnect.setCreateTime(createTime);
            communityLabelConnectMapper.insert(communityLabelConnect);
        }

        // 添加至社区角色
        CommunityRole communityRole = new CommunityRole();
        communityRole.setRoleName(CommunityRoleEnum.MEMBER.getMsg());
        communityRole.setCreateTime(createTime);
        communityRole.setUpdateTime(createTime);
        communityRole.setCommunityId(communityId);
        communityRoleMapper.insert(communityRole);

        Long roleId = communityRole.getId();
        // 添加至社区角色关系表
        CommunityRoleConnect communityRoleConnect = new CommunityRoleConnect();
        communityRoleConnect.setCommunityId(communityId);
        communityRoleConnect.setRoleId(roleId);
        communityRoleConnect.setCreateTime(createTime);
        communityRoleConnectMapper.insert(communityRoleConnect);
        // 添加社区角色关系表
        CommunityUserRoleConnect communityUserRoleConnect = new CommunityUserRoleConnect();
        communityUserRoleConnect.setCommunityId(communityId);
        communityUserRoleConnect.setRoleId(roleId);
        communityUserRoleConnect.setCreateTime(createTime);
        communityUserRoleConnect.setUpdateTime(createTime);
        communityUserRoleConnect.setUserId(userId);
        communityUserRoleConnectMapper.insert(communityUserRoleConnect);

        // 添加至社区管理
        CommunityUserManage communityUserManage = new CommunityUserManage();
        communityUserManage.setCommunityId(communityId);
        communityUserManage.setUserId(userId);
        communityUserManage.setCreateTime(createTime);
        communityUserManage.setIsPrime(CommunityEnum.IS_PRIME_MANAGE.getCode());
        communityUserManageMapper.insert(communityUserManage);

        // 添加全部社区频道
        CommunityChannel communityChannel = new CommunityChannel();
        communityChannel.setCommunityId(communityId);
        communityChannel.setChannelName(CommunityChannelEnum.ALL.getChannelName());
        communityChannel.setSort(CommunityChannelEnum.ALL.getSort());
        communityChannel.setSupportShow(CommunityEnum.NOT_SUPPORT_SHOW.getCode());
        communityChannel.setSupportManageModify(CommunityEnum.NOT_SUPPORT_MANAGE_MODIFY.getCode());
        communityChannel.setCreateTime(createTime);
        communityChannel.setUpdateTime(createTime);
        communityChannelMapper.insert(communityChannel);

        // 添加问答社区频道
        CommunityChannel communityChannel1 = new CommunityChannel();
        communityChannel1.setCommunityId(communityId);
        communityChannel1.setChannelName(CommunityChannelEnum.QUESTION.getChannelName());
        communityChannel1.setCreateTime(createTime);
        communityChannel1.setUpdateTime(createTime);
        communityChannelMapper.insert(communityChannel1);

        // 添加交流社区频道
        CommunityChannel communityChannel2 = new CommunityChannel();
        communityChannel2.setCommunityId(communityId);
        communityChannel2.setChannelName(CommunityChannelEnum.DISCUSS.getChannelName());
        communityChannel2.setCreateTime(createTime);
        communityChannel2.setUpdateTime(createTime);
        communityChannelMapper.insert(communityChannel2);

        // 添加活动社区频道
        CommunityChannel communityChannel3 = new CommunityChannel();
        communityChannel3.setCommunityId(communityId);
        communityChannel3.setChannelName(CommunityChannelEnum.ACTIVITY.getChannelName());
        communityChannel3.setCreateTime(createTime);
        communityChannel3.setUpdateTime(createTime);
        communityChannelMapper.insert(communityChannel3);

        // 加入elasticsearch索引
        // 得到社区属性信息
        Long attributeLabelId = community.getAttributeLabelId();
        CommunityAttribute communityAttribute = communityAttributeMapper.selectById(attributeLabelId);
        community.setAttributeLabelName(communityAttribute.getName());

        // 得到社区分类标签
        Long classificationId = community.getClassificationId();
        CommunityClassificationLabel communityClassificationLabel = communityClassificationLabelMapper.selectById(classificationId);
        community.setClassificationName(communityClassificationLabel.getName());

        // 得到社区内容标签
        QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
        communityLabelConnectQueryWrapper.eq("community_id", communityId);
        communityLabelConnectQueryWrapper.select("community_classification_label_id");
        List<Object> labelIdList = communityLabelConnectMapper.selectObjs(communityLabelConnectQueryWrapper);
        if (labelIdList != null || labelIdList.size() > 0) {
            QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
            communityClassificationLabelQueryWrapper.in("id", labelIdList);
            List<CommunityClassificationLabel> communityClassificationLabels = communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper);
            List<String> contentLabelName = new ArrayList<>();
            for (CommunityClassificationLabel classificationLabel : communityClassificationLabels) {
                contentLabelName.add(classificationLabel.getName());
            }
            community.setContentLabelName(contentLabelName);
        }

        communityToSearchFeign.createCommunity(JSONObject.toJSONString(community));
    }


    /**
     * 邀请用户加入社区
     *
     * @param communityId 社区id
     * @param nowUserId 邀请用用户id
     * @param inviteUserId 被邀请用户id
     * @param inviteRoleId 被邀请用户角色id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/1 10:35
     */
    private void inviteUserEnterCommunity(Long communityId, Long nowUserId, Long inviteUserId, Long inviteRoleId) {
        CommunityUserInvite communityUserInvite = new CommunityUserInvite();
        communityUserInvite.setCommunityId(communityId);
        communityUserInvite.setUserId(nowUserId);
        communityUserInvite.setInviteId(inviteUserId);
        communityUserInvite.setRoleId(inviteRoleId);
        Date date = new Date();
        communityUserInvite.setUpdateTime(date);
        communityUserInvite.setCreateTime(date);
        communityUserInviteMapper.insert(communityUserInvite);
    }

    /**
     * 加入社区用户申请表
     *
     * @param userId 加入用户id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/1 10:10
     */
    private void addCommunityUserApplication(Long userId, Long communityId) {
        CommunityUserApplication communityUserApplication = new CommunityUserApplication();
        Date date = new Date();
        communityUserApplication.setCommunityId(communityId);
        communityUserApplication.setUserId(userId);
        communityUserApplication.setCreateTime(date);
        communityUserApplication.setUpdateTime(date);
        communityUserApplication.setStatus(CommunityEnum.REVIEW_PROGRESS.getCode());
        communityUserApplicationMapper.insert(communityUserApplication);
    }


    /**
     * 删除文章评论
     *
     * @param commentId 评论id
     * @param communityArticleId 社区评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 11:11
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long communityArticleId) {
        long sum = 0;
        int deleteById = communityArticleCommentMapper.deleteById(commentId);
        // 删掉所有的子评论
        QueryWrapper<CommunityArticleComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("parent_id", commentId);
        List<CommunityArticleComment> communityArticleCommentList = communityArticleCommentMapper.selectList(commentQueryWrapper);
        if (communityArticleCommentList != null && communityArticleCommentList.size() > 0) {
            List<Long> commentIdList = new ArrayList<>();
            for (CommunityArticleComment comment : communityArticleCommentList) {
                Long id = comment.getId();
                commentIdList.add(id);
            }

            QueryWrapper<CommunityArticleComment> articleCommentQueryWrapper = new QueryWrapper<>();
            articleCommentQueryWrapper.in("id", commentIdList);
            int delete = communityArticleCommentMapper.delete(articleCommentQueryWrapper);

            UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", communityArticleId);
            updateWrapper.setSql("comment_count = comment_count - " + (delete + deleteById));
            communityArticleMapper.update(null, updateWrapper);
            sum = delete + deleteById;
            // 删除子评论点赞
            QueryWrapper<CommunityArticleCommentLike> articleCommentLikeQueryWrapper = new QueryWrapper<>();
            articleCommentLikeQueryWrapper.in("community_article_comment_id", commentIdList);
            communityArticleCommentLikeMapper.delete(articleCommentLikeQueryWrapper);
        } else {
            UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", communityArticleId);
            updateWrapper.setSql("comment_count = comment_count - "  + deleteById);
            sum = deleteById;
            communityArticleMapper.update(null, updateWrapper);
        }

        // 删除评论点赞
        QueryWrapper<CommunityArticleCommentLike> commentLikeQueryWrapper=  new QueryWrapper<>();
        commentLikeQueryWrapper.eq("community_article_comment_id", commentId);
        communityArticleCommentLikeMapper.delete(commentLikeQueryWrapper);


        communityToSearchFeign.communityArticleCommentSub(communityArticleId, sum);
    }

    /**
     * 删除任务历史记录
     *
     * @param communityArticleTaskReplyId 社区文章任务回复id
     * @param communityArticleTaskId 社区文章任务id
     * @author wusihao
     * @date 2023/9/21 16:49
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommunityArticleTaskReply(Long communityArticleTaskId, Long communityArticleTaskReplyId) {
        communityArticleTaskReplyMapper.deleteById(communityArticleTaskReplyId);
        // 社区文章任务回复数 - 1
        UpdateWrapper<CommunityArticleTask> communityArticleTaskUpdateWrapper = new UpdateWrapper<>();
        communityArticleTaskUpdateWrapper.eq("id", communityArticleTaskId);
        communityArticleTaskUpdateWrapper.setSql("reply_count = reply_count - 1");
        communityArticleTaskMapper.update(null, communityArticleTaskUpdateWrapper);
    }

    /**
     * 社区文章评分
     *
     * @param communityArticleId 社区文章id
     * @param articleScore 文章评分
     * @param userId 评价者id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 10:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void communityArticleScore(Long userId, Integer articleScore, Long communityArticleId, Long communityId) {
        // 判断当前用户是否已评论该文章
        QueryWrapper<CommunityArticleScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("community_article_id", communityArticleId);
        CommunityArticleScore selectOne = communityArticleScoreMapper.selectOne(queryWrapper);
        Date createTime = new Date();
        if (selectOne == null) {
            // 插入社区评论表
            CommunityArticleScore communityArticleScore = new CommunityArticleScore();
            communityArticleScore.setUserId(userId);
            communityArticleScore.setCommunityArticleId(communityArticleId);
            communityArticleScore.setScore(articleScore);
            communityArticleScore.setCreateTime(createTime);
            communityArticleScore.setUpdateTime(createTime);
            communityArticleScoreMapper.insert(communityArticleScore);
        } else {
            // 更新社区文章评论表
            selectOne.setScore(articleScore);
            selectOne.setUpdateTime(createTime);
            communityArticleScoreMapper.updateById(selectOne);
        }

        // 更新社区文章评分信息
        QueryWrapper<CommunityArticleScore> communityArticleScoreQueryWrapper = new QueryWrapper<>();
        communityArticleScoreQueryWrapper.eq("community_article_id", communityArticleId);
        communityArticleScoreQueryWrapper.select("score");
        List<CommunityArticleScore> communityArticleScores = communityArticleScoreMapper.selectList(communityArticleScoreQueryWrapper);
        int totalCount = 0;
        int totalScore = 0;
        for (CommunityArticleScore communityArticleScore : communityArticleScores) {
            totalCount ++ ;
            totalScore += communityArticleScore.getScore();
        }

        float resScore = (float) totalScore / totalCount;
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("score", resScore);
        updateWrapper.set("score_count", totalCount);
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.updateCourseScore(communityArticleId, resScore);
    }

    /**
     * 删除社区文章
     *
     * @param  communityId 社区id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 17:33
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommunityArticle(Long communityId, Long communityArticleId) {
        communityArticleMapper.deleteById(communityArticleId);
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.setSql("article_count = article_count - 1");
        communityMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleSubOne(communityId);
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

        communityToSearchFeign.communityArticleSubOne(communityId);
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
     * @param communityArticleVote 文章投票实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 8:45
     */
    private void insertToArticleVote(CommunityArticleVote communityArticleVote, Long userId) {
        Date date = new Date();
        List<CommunityArticleVoteItem> communityArticleVoteItemList = communityArticleVote.getCommunityArticleVoteItemList();
        int communityArticleVoteLen = communityArticleVoteItemList.size();
        communityArticleVote.setVotePeople(communityArticleVoteLen);
        communityArticleVote.setUpdateTime(date);
        communityArticleVote.setCreateTime(date);
        communityArticleVoteMapper.insert(communityArticleVote);
        Long communityArticleVoteId = communityArticleVote.getId();

        for (CommunityArticleVoteItem communityArticleVoteItem : communityArticleVoteItemList) {
            communityArticleVoteItem.setCommunityArticleVoteId(communityArticleVoteId);
            communityArticleVoteItem.setCreateTime(date);
            communityArticleVoteItemMapper.insert(communityArticleVoteItem);
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
        String event = jsonObject.getString("event");
        log.error("发送错误事件: event ==> {}, 错误原因为 ==> {}", event, e.getMessage());
        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setErrorEvent(event);
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

        communityToSearchFeign.communityArticleAddOne(communityId);
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
        updateWrapper.setSql("member_count = member_count - 1");
        communityMapper.update(null, updateWrapper);

        communityToSearchFeign.communityMemberSubOne(communityId);
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
        updateWrapper.setSql("member_count = member_count + 1");
        communityMapper.update(null, updateWrapper);

        communityToSearchFeign.communityMemberAddOne(communityId);
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
            CommunityArticleVote communityArticleVote = communityArticleVo.getCommunityArticleVote();
            communityArticleVote.setCommunityArticleId(articleId);
            insertToArticleVote(communityArticleVote, userId);
        }

        // 社区文章数 + 1
        UpdateWrapper<Community> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityId);
        updateWrapper.setSql("article_count = article_count + 1");
        communityMapper.update(null, updateWrapper);
    }


    /**
     * 修改社区文章频道
     *
     * @param channelId 频道id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:47
     */
    private void updateCommunityArticleChannel(Long channelId, Long communityArticleId) {
        UpdateWrapper<CommunityArticle> communityArticleUpdateWrapper = new UpdateWrapper<>();
        communityArticleUpdateWrapper.eq("id", communityArticleId);
        communityArticleUpdateWrapper.set("channel_id", channelId);
        communityArticleMapper.update(null, communityArticleUpdateWrapper);
    }

    /**
     * 社区文章取消置顶
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:13
     */
    private void cancelCommunityArticleTop(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("is_top", CommunityEnum.NOT_TOP.getCode());
        communityArticleMapper.update(null, updateWrapper);
    }

    /**
     * 社区文章置顶
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:12
     */
    private void communityArticleTop(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("is_top", CommunityEnum.IS_TOP.getCode());
        communityArticleMapper.update(null, updateWrapper);
    }

    /**
     * 社区取消文章精选
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:10
     */
    private void cancelCommunityArticleExcellent(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("is_excellent", CommunityEnum.NOT_EXCELLENT.getCode());
        communityArticleMapper.update(null, updateWrapper);
    }

    /**
     * 社区文章精选
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:10
     */
    private void communityArticleExcellent(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("is_excellent", CommunityEnum.IS_EXCELLENT.getCode());
        communityArticleMapper.update(null, updateWrapper);
    }

    /**
     * 社区文章收藏数 - 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:43
     */
    private void communityArticleCollectCountSubOne(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.setSql("collect_count = collect_count - 1");
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleCollectCountSubOne(communityArticleId);
    }

    /**
     * 社区文章收藏数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:43
     */
    private void communityArticleCollectCountAddOne(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.setSql("collect_count = collect_count + 1");
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleCollectCountAddOne(communityArticleId);
    }

    /**
     * 社区文章评论数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 14:34
     */
    private void communityArticleCommentAddOne(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.setSql("comment_count = comment_count + 1");
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleCommentCountAdd(communityArticleId);
    }

    /**
     * 取消置顶评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 11:17
     */
    private void cancelTopComment(Long commentId) {
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_top", CommunityEnum.NOT_TOP.getCode());
        communityArticleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 置顶评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 11:16
     */
    private void topComment(Long commentId) {
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_top", CommunityEnum.IS_TOP.getCode());
        communityArticleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 取消精选评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 11:15
     */
    private void cancelCurationComment(Long commentId) {
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_curation", CommunityEnum.NOT_EXCELLENT.getCode());
        communityArticleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 精选评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 11:14
     */
    private void curationComment(Long commentId) {
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_curation", CommunityEnum.IS_EXCELLENT.getCode());
        communityArticleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 社区文章任务回复数 + 1
     *
     * @param communityArticleTaskId 社区文章任务id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 17:21
     */
    private void communityArticleTaskReplyCountAddOne(Long communityArticleTaskId) {
        UpdateWrapper<CommunityArticleTask> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleTaskId);
        updateWrapper.setSql("reply_count = reply_count + 1");
        communityArticleTaskMapper.update(null, updateWrapper);
    }

    /**
     * 社区文章游览数 + 1
     *
     * @param communityArticleId 社区文章 id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 9:19
     */
    private void communityArticleViewCountAddOne(Long communityArticleId) {
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.setSql("view_count = view_count + 1");
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleViewAddOne(communityArticleId);
    }

    /**
     * 社区文章投票人数 + 1
     *
     * @param communityArticleVoteId 社区文章投票id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 17:29
     */
    private void communityArticleVotePeopleAddOne(Long communityArticleVoteId) {
        UpdateWrapper<CommunityArticleVote> communityArticleVoteUpdateWrapper =  new UpdateWrapper<>();
        communityArticleVoteUpdateWrapper.eq("id", communityArticleVoteId);
        communityArticleVoteUpdateWrapper.setSql("vote_people = vote_people + 1");
        communityArticleVoteMapper.update(null, communityArticleVoteUpdateWrapper);
    }

    /**
     * 社区文章取消点赞
     *
     * @param userId 当前用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 13:13
     */
    @Transactional(rollbackFor = Exception.class)
    public void communityArticleCancelLike(Long userId, Long communityArticleId) {
        QueryWrapper<CommunityArticleLike> communityArticleLikeQueryWrapper = new QueryWrapper<>();
        communityArticleLikeQueryWrapper.eq("user_id", userId);
        communityArticleLikeQueryWrapper.eq("community_article_id", communityArticleId);
        communityArticleLikeMapper.delete(communityArticleLikeQueryWrapper);

        // 社区文章点赞数 - 1
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.setSql("like_count = like_count - 1");
        communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleLikeCountSubOne(communityArticleId);
    }

    /**
     * 社区文章点赞
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 13:10
     */
    public void communityArticleLike(Long userId, Long communityArticleId) {
            CommunityArticleLike communityArticleLike = new CommunityArticleLike();
            communityArticleLike.setUserId(userId);
            communityArticleLike.setCommunityArticleId(communityArticleId);
            communityArticleLike.setCreateTime(new Date());
            communityArticleLikeMapper.insert(communityArticleLike);
            // 社区文章点赞数 + 1
            UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", communityArticleId);
            updateWrapper.setSql("like_count = like_count + 1");
            communityArticleMapper.update(null, updateWrapper);

        communityToSearchFeign.communityArticleLikeCountAddOne(communityArticleId);
    }

    /**
     * 取消评论点赞
     *
     * @param userId 点赞用户
     * @param commentId 评论id
     * @author wusihao
     * @date 2023/9/23 16:51
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelCommentLike(Long userId, Long commentId) {
        QueryWrapper<CommunityArticleCommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("user_id", userId);
        commentLikeQueryWrapper.eq("community_article_comment_id", commentId);
        communityArticleCommentLikeMapper.delete(commentLikeQueryWrapper);

        // 点赞数 - 1
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.setSql("like_count = like_count - 1");
        communityArticleCommentMapper.update(null, updateWrapper);
    }

    /**
     * 评论点赞
     *
     * @param userId 点赞用户
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 16:50
     */
    @Transactional(rollbackFor = Exception.class)
    public void commentLike(Long userId, Long commentId) {
        CommunityArticleCommentLike communityArticleCommentLike = new CommunityArticleCommentLike();
        communityArticleCommentLike.setCreateTime(new Date());
        communityArticleCommentLike.setCommunityArticleCommentId(commentId);
        communityArticleCommentLike.setUserId(userId);
        communityArticleCommentLikeMapper.insert(communityArticleCommentLike);

        // 点赞数 - 1
        UpdateWrapper<CommunityArticleComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.setSql("like_count = like_count + 1");
        communityArticleCommentMapper.update(null, updateWrapper);
    }
}

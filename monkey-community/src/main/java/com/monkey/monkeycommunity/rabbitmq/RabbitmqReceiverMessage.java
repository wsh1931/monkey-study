package com.monkey.monkeycommunity.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
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
                this.communityArticleCountAddOne(jsonObject);
            } else if (EventConstant.communityMemberCountAddOne.equals(event)) {
                // 社区成员数 + 1
                Long communityId = jsonObject.getLong("communityId");
                this.communityMemberCountAddone(communityId);
            } else if (EventConstant.getCommunityMemberCountSubOne.equals(event)) {
                // 社区成员数 - 1
                Long communityId = jsonObject.getLong("communityId");
                this.communityMemberCountSubOne(communityId);
            } else if (EventConstant.communityArticleCountSubOne.equals(event)) {
                Long communityId = jsonObject.getLong("communityId");
                log.info("社区文章数减一：communityId：{}", communityId);
                this.communityArticleCountSubOne(communityId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
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
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
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
        updateWrapper.setSql("like_count = like_count + 1");
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
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
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

        double resScore = (double) totalScore / totalCount;
        UpdateWrapper<CommunityArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", communityArticleId);
        updateWrapper.set("score", resScore);
        updateWrapper.set("score_count", totalCount);
        communityArticleMapper.update(null, updateWrapper);
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
                this.deleteCommunityArticle(communityArticleId);
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
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            this.addToRabbitmqErrorLog(message, e);
            // 注意不能throw不然队列会一直接收消息
        }
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
        int deleteById = communityArticleCommentMapper.deleteById(commentId);
        // 删掉所有的子评论
        QueryWrapper<CommunityArticleComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("parent_id", commentId);
        List<CommunityArticleComment> communityArticleCommentList = communityArticleCommentMapper.selectList(commentQueryWrapper);
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
        int i = 1 / 0;
        // 删除评论点赞
        QueryWrapper<CommunityArticleCommentLike> commentLikeQueryWrapper=  new QueryWrapper<>();
        commentLikeQueryWrapper.eq("community_article_comment_id", commentId);
        communityArticleCommentLikeMapper.delete(commentLikeQueryWrapper);

        // 删除子评论点赞
        QueryWrapper<CommunityArticleCommentLike> articleCommentLikeQueryWrapper = new QueryWrapper<>();
        articleCommentLikeQueryWrapper.in("community_article_comment_id", commentIdList);
        communityArticleCommentLikeMapper.delete(articleCommentLikeQueryWrapper);
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
                this.deleteCommunityArticle(communityArticleId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            this.addToRabbitmqErrorLog(message, e);
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
            CommunityArticleVote communityArticleVote = communityArticleVo.getCommunityArticleVote();
            communityArticleVote.setCommunityArticleId(articleId);
            insertToArticleVote(communityArticleVote, userId);
        }
    }
}

package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleCommentLikeMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleCommentMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityArticleComment;
import com.monkey.monkeycommunity.pojo.CommunityArticleCommentLike;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.CommunityCommentService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/22 16:18
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityCommentServiceImpl implements CommunityCommentService {
    @Resource
    private CommunityArticleCommentMapper communityArticleCommentMapper;
    @Resource
    private CommunityArticleCommentLikeMapper communityArticleCommentLikeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    /**
     * 查询默认排序评论列表
     *
     * @param userId             当前登录用户id
     * @param communityArticleId 社区文章id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/22 16:31
     */
    @Override
    public R queryDefaultCommentList(String userId, Long communityArticleId, Long currentPage, Long pageSize) {
        QueryWrapper<CommunityArticleComment> communityArticleCommentQueryWrapper = new QueryWrapper<>();
        communityArticleCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        communityArticleCommentQueryWrapper.orderByDesc("is_top");
        communityArticleCommentQueryWrapper.orderByDesc("like_count");
        communityArticleCommentQueryWrapper.orderByAsc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleCommentMapper.selectPage(page, communityArticleCommentQueryWrapper);
        List<CommunityArticleComment> communityArticleCommentList = selectPage.getRecords();
        JSONObject communityDetailInfo = this.getCommunityDetailInfo(userId, communityArticleCommentList);
        Long commentCount = communityDetailInfo.getLong("commentCount");
        List<CommunityArticleComment> articleCommentList =
                JSONObject.parseArray(communityDetailInfo.getString("communityArticleCommentList"), CommunityArticleComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 判断当前登录用户是否是该文章的作者
     *
     * @param userId             当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 9:19
     */
    @Override
    public R judgeIsAuthor(String userId, Long communityArticleId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(CommunityEnum.NOT_AUTHOR.getCode());
        }

        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        Long articleUserId = communityArticle.getUserId();
        if (articleUserId.equals(Long.parseLong(userId))) {
            return R.ok(CommunityEnum.IS_AUTHOR.getCode());
        }
        return R.ok(CommunityEnum.NOT_AUTHOR.getCode());
    }

    /**
     * 查询时间升序评论列表
     *
     * @param userId             当前登录用户id
     * @param communityArticleId 社区文章id
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 9:44
     */
    @Override
    public R queryTimeUpgradeComment(String userId, Long communityArticleId, Long currentPage, Long pageSize) {
        QueryWrapper<CommunityArticleComment> communityArticleCommentQueryWrapper = new QueryWrapper<>();
        communityArticleCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        communityArticleCommentQueryWrapper.orderByAsc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleCommentMapper.selectPage(page, communityArticleCommentQueryWrapper);
        List<CommunityArticleComment> communityArticleCommentList = selectPage.getRecords();
        JSONObject communityDetailInfo = this.getCommunityDetailInfo(userId, communityArticleCommentList);
        Long commentCount = communityDetailInfo.getLong("commentCount");
        List<CommunityArticleComment> articleCommentList =
                JSONObject.parseArray(communityDetailInfo.getString("communityArticleCommentList"), CommunityArticleComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 得到时间降序评论列表
     *
     * @param userId             当前登录用户id
     * @param communityArticleId 社区文章id
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:00
     */
    @Override
    public R queryTimeDownSortComment(String userId, Long communityArticleId, Long currentPage, Long pageSize) {
        QueryWrapper<CommunityArticleComment> communityArticleCommentQueryWrapper = new QueryWrapper<>();
        communityArticleCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        communityArticleCommentQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleCommentMapper.selectPage(page, communityArticleCommentQueryWrapper);
        List<CommunityArticleComment> communityArticleCommentList = selectPage.getRecords();
        JSONObject communityDetailInfo = this.getCommunityDetailInfo(userId, communityArticleCommentList);
        Long commentCount = communityDetailInfo.getLong("commentCount");
        List<CommunityArticleComment> articleCommentList =
                JSONObject.parseArray(communityDetailInfo.getString("communityArticleCommentList"), CommunityArticleComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 查询未回复评论集合
     *
     * @param userId             当前登录用户id
     * @param communityArticleId 社区文章id
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:11
     */
    @Override
    public R queryNotReplyCommentList(String userId, Long communityArticleId, Long currentPage, Long pageSize) {
        QueryWrapper<CommunityArticleComment> communityArticleCommentQueryWrapper = new QueryWrapper<>();
        communityArticleCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        communityArticleCommentQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleCommentMapper.selectPage(page, communityArticleCommentQueryWrapper);
        List<CommunityArticleComment> communityArticleCommentList = selectPage.getRecords();
        long commentCount= 0;
        for (CommunityArticleComment communityArticleComment : communityArticleCommentList) {
            Long oneCommentId = communityArticleComment.getId();
            // 通过一级评论id得到下面得多级评论
            QueryWrapper<CommunityArticleComment> twoCommentQueryWrapper = new QueryWrapper<>();
            twoCommentQueryWrapper.orderByAsc("create_time");
            twoCommentQueryWrapper.eq("parent_id", oneCommentId);
            List<CommunityArticleComment> twoCommentList = communityArticleCommentMapper.selectList(twoCommentQueryWrapper);

            // 判断当前登录用户是否回复此评论
            boolean existReply = false;
            if (userId != null && !"".equals(userId)) {
                for (CommunityArticleComment comment : twoCommentList) {
                    Long replyId = comment.getReplyId();
                    if (replyId.equals(Long.parseLong(userId))) {
                        existReply = true;
                    }
                }
            }

            // 如果存在回复删除此评论
            if (existReply) {
                communityArticleCommentList.remove(communityArticleComment);
                continue;
            }

            commentCount ++ ;
            // 判断当前登录用户是否点赞一级评论
            if (userId != null && !"".equals(userId)) {
                QueryWrapper<CommunityArticleCommentLike> oneCommentLikeQueryWrapper = new QueryWrapper<>();
                oneCommentLikeQueryWrapper.eq("user_id", userId);
                oneCommentLikeQueryWrapper.eq("community_article_comment_id", oneCommentId);
                Long selectCount = communityArticleCommentLikeMapper.selectCount(oneCommentLikeQueryWrapper);
                if (selectCount > 0) {
                    communityArticleComment.setIsLike(CommunityEnum.IS_LIKE.getCode());
                } else {
                    communityArticleComment.setIsLike(CommunityEnum.NOT_LIKE.getCode() );
                }
            } else {
                communityArticleComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
            }

            // 得到发送人姓名头像
            Long senderId = communityArticleComment.getSenderId();
            User user = userMapper.selectById(senderId);
            communityArticleComment.setSenderUsername(user.getUsername());
            communityArticleComment.setSenderHeadImg(user.getPhoto());
            // 得到多级评论详细信息
            for (CommunityArticleComment twoComment : twoCommentList) {
                commentCount ++ ;
                Long twoCommentId = twoComment.getId();
                // 判断当前登录用户是否点赞二级评论
                if (userId != null && !"".equals(userId)) {
                    QueryWrapper<CommunityArticleCommentLike> twoCommentLikeQueryWrapper = new QueryWrapper<>();
                    twoCommentLikeQueryWrapper.eq("community_article_comment_id", twoCommentId);
                    twoCommentLikeQueryWrapper.eq("user_id", userId);
                    Long selectCount = communityArticleCommentLikeMapper.selectCount(twoCommentLikeQueryWrapper);
                    if (selectCount > 0) {
                        twoComment.setIsLike(CommunityEnum.IS_LIKE.getCode());
                    } else {
                        twoComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
                    }
                } else {
                    twoComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
                }

                // 得到二级评论发送人信息
                Long twoCommentSenderId = twoComment.getSenderId();
                User twoSender = userMapper.selectById(twoCommentSenderId);
                twoComment.setSenderUsername(twoSender.getUsername());
                twoComment.setReplyHeadImg(twoSender.getPhoto());

                // 得到二级评论回复人信息
                Long replyId = twoComment.getReplyId();
                User replyUser = userMapper.selectById(replyId);
                twoComment.setReplyUsername(replyUser.getUsername());
                twoComment.setReplyHeadImg(replyUser.getPhoto());
            }

            communityArticleComment.setCommunityArticleCommentList(twoCommentList);
        }

        selectPage.setRecords(communityArticleCommentList);
        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 精选评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:52
     */
    @Override
    public R curationComment(Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.curationComment);
        data.put("commentId", commentId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 取消精选评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:55
     */
    @Override
    public R cancelCurationComment(Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.cancelCurationComment);
        data.put("commentId", commentId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 置顶评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:55
     */
    @Override
    public R topComment(Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.topComment);
        data.put("commentId", commentId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 取消置顶评论
     *
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:55
     */
    @Override
    public R cancelTopComment(Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.cancelTopComment);
        data.put("commentId", commentId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @param communityArticleId 社区文章评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:56
     */
    @Override
    public R deleteComment(Long commentId, Long communityArticleId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.deleteComment);
        data.put("commentId", commentId);
        data.put("communityArticleId", communityArticleId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDeleteDirectExchange,
                RabbitmqRoutingName.communityDeleteRouting, message);
        return R.ok();
    }

    /**
     * 发表社区文章评论
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 14:26
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R publishComment(long userId, Long communityArticleId, String commentContent) {
        CommunityArticleComment communityArticleComment = new CommunityArticleComment();
        communityArticleComment.setCommunityArticleId(communityArticleId);
        communityArticleComment.setContent(commentContent);
        communityArticleComment.setSenderId(userId);
        communityArticleComment.setCreateTime(new Date());
        communityArticleCommentMapper.insert(communityArticleComment);

        // 社区文章回复数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.communityArticleCommentAddOne);
        data.put("communityArticleId", communityArticleId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 发表评论回复
     *
     * @param senderId           发送者id
     * @param parentId           评论父id
     * @param replyId            回复者id
     * @param replyContent       回复内容
     * @param communityArticleId
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 16:28
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public R publishCommentReply(Long senderId, Long parentId, long replyId, String replyContent, Long communityArticleId) {
        CommunityArticleComment communityArticleComment = new CommunityArticleComment();
        communityArticleComment.setReplyId(replyId);
        communityArticleComment.setSenderId(senderId);
        communityArticleComment.setCreateTime(new Date());
        communityArticleComment.setContent(replyContent);
        communityArticleComment.setParentId(parentId);
        communityArticleComment.setCommunityArticleId(communityArticleId);
        communityArticleCommentMapper.insert(communityArticleComment);
        // 社区文章回复数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.communityArticleCommentAddOne);
        data.put("communityArticleId", communityArticleId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);

        return R.ok();
    }

    /**
     * 评论点赞
     *
     * @param userId 当前登录用户id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 16:45
     */
    @Override
    public R commentLike(long userId, Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.commentLike);
        data.put("commentId", commentId);
        data.put("userId", userId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }

    /**
     * 取消评论点赞
     *
     * @param userId 当前登录用户id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 16:45
     */
    @Override
    public R cancelCommentLike(long userId, Long commentId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.cancelCommentLike);
        data.put("commentId", commentId);
        data.put("userId", userId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }


    /**
     * 得到评论详细信息
     *
     * @param userId 当前登录用户id
     * @param communityArticleCommentList 社区文章评论集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/22 16:44
     */
    private JSONObject getCommunityDetailInfo(String userId, List<CommunityArticleComment> communityArticleCommentList) {
        long commentCount= 0;
        for (CommunityArticleComment communityArticleComment : communityArticleCommentList) {
            commentCount ++ ;
            Long oneCommentId = communityArticleComment.getId();
            // 判断当前登录用户是否点赞一级评论
            if (userId != null && !"".equals(userId)) {
                QueryWrapper<CommunityArticleCommentLike> oneCommentLikeQueryWrapper = new QueryWrapper<>();
                oneCommentLikeQueryWrapper.eq("user_id", userId);
                oneCommentLikeQueryWrapper.eq("community_article_comment_id", oneCommentId);
                Long selectCount = communityArticleCommentLikeMapper.selectCount(oneCommentLikeQueryWrapper);
                if (selectCount > 0) {
                    communityArticleComment.setIsLike(CommunityEnum.IS_LIKE.getCode());
                } else {
                    communityArticleComment.setIsLike(CommunityEnum.NOT_LIKE.getCode() );
                }
            } else {
                communityArticleComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
            }

            // 得到发送人姓名头像
            Long senderId = communityArticleComment.getSenderId();
            User user = userMapper.selectById(senderId);
            communityArticleComment.setSenderUsername(user.getUsername());
            communityArticleComment.setSenderHeadImg(user.getPhoto());

            // 通过一级评论id得到下面得多级评论
            QueryWrapper<CommunityArticleComment> twoCommentQueryWrapper = new QueryWrapper<>();
            twoCommentQueryWrapper.orderByAsc("create_time");
            twoCommentQueryWrapper.eq("parent_id", oneCommentId);
            List<CommunityArticleComment> twoCommentList = communityArticleCommentMapper.selectList(twoCommentQueryWrapper);
            // 得到多级评论详细信息
            for (CommunityArticleComment twoComment : twoCommentList) {
                commentCount ++ ;
                Long twoCommentId = twoComment.getId();
                // 判断当前登录用户是否点赞二级评论
                if (userId != null && !"".equals(userId)) {
                    QueryWrapper<CommunityArticleCommentLike> twoCommentLikeQueryWrapper = new QueryWrapper<>();
                    twoCommentLikeQueryWrapper.eq("community_article_comment_id", twoCommentId);
                    twoCommentLikeQueryWrapper.eq("user_id", userId);
                    Long selectCount = communityArticleCommentLikeMapper.selectCount(twoCommentLikeQueryWrapper);
                    if (selectCount > 0) {
                        twoComment.setIsLike(CommunityEnum.IS_LIKE.getCode());
                    } else {
                        twoComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
                    }
                } else {
                    twoComment.setIsLike(CommunityEnum.NOT_LIKE.getCode());
                }

                // 得到二级评论发送人信息
                Long twoCommentSenderId = twoComment.getSenderId();
                User twoSender = userMapper.selectById(twoCommentSenderId);
                twoComment.setSenderUsername(twoSender.getUsername());
                twoComment.setReplyHeadImg(twoSender.getPhoto());

                // 得到二级评论回复人信息
                Long replyId = twoComment.getReplyId();
                User replyUser = userMapper.selectById(replyId);
                twoComment.setReplyUsername(replyUser.getUsername());
                twoComment.setReplyHeadImg(replyUser.getPhoto());
            }

            communityArticleComment.setCommunityArticleCommentList(twoCommentList);
        }

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("communityArticleCommentList", JSONObject.toJSONString(communityArticleCommentList));
        return data;
    }
}

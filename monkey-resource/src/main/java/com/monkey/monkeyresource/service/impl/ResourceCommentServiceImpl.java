package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.mapper.ResourceCommentLikeMapper;
import com.monkey.monkeyresource.mapper.ResourceCommentMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceComment;
import com.monkey.monkeyresource.pojo.ResourceCommentLike;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.ResourceCommentService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * @author: wusihao
 * @date: 2023/10/18 16:39
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceCommentServiceImpl implements ResourceCommentService {
    @Resource
    private ResourceCommentMapper resourceCommentMapper;
    @Resource
    private ResourceCommentLikeMapper resourceCommentLikeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ResourcesMapper resourcesMapper;

    /**
     * 发表评论方法
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @param commentContent 用户评论内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 17:05
     */
    @Override
    @Transactional
    public R publishCommentMethod(Long resourceId, long userId, String commentContent) {
        ResourceComment resourceComment = new ResourceComment();
        resourceComment.setSenderId(userId);
        resourceComment.setContent(commentContent);
        resourceComment.setResourceId(resourceId);
        resourceComment.setCreateTime(new Date());
        resourceCommentMapper.insert(resourceComment);

        // 资源评论数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.resourceCommentCountAddOne);
        data.put("resourceId", resourceId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);

        // 插入资源消息评论表
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.commentInsertResourceMessage);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("senderId", userId);
        jsonObject.put("commentContent", commentContent);
        Message message1 = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message1);
        return R.ok();
    }

    /**
     * 查询评论列表
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 21:18
     */
    @Override
    public R queryCommentList(Long resourceId, String userId, Long currentPage, Integer pageSize) {
        QueryWrapper<ResourceComment> resourceCommentQueryWrapper = new QueryWrapper<>();
        resourceCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        resourceCommentQueryWrapper.orderByDesc("is_top");
        resourceCommentQueryWrapper.orderByDesc("is_curation");
        resourceCommentQueryWrapper.orderByDesc("like_count");
        resourceCommentQueryWrapper.orderByAsc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourceCommentMapper.selectPage(page, resourceCommentQueryWrapper);
        List<ResourceComment> resourceCommentList = selectPage.getRecords();
        JSONObject commentDetail = this.getCommentDetail(userId, resourceCommentList);
        Long commentCount = commentDetail.getLong("commentCount");
        List<ResourceComment> articleCommentList =
                JSONObject.parseArray(commentDetail.getString("resourceCommentList"), ResourceComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 判断当前登录用户是否是文章作者
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/19 14:33
     */
    @Override
    public R judgeIsAuthor(Long resourceId, String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(ResourcesEnum.NOT_Author.getCode());
        }

        Resources resources = resourcesMapper.selectById(resourceId);
        Long id = resources.getUserId();
        if (id.equals(Long.parseLong(userId))) {
            return R.ok(ResourcesEnum.is_Author.getCode());
        }
        return R.ok(ResourcesEnum.NOT_Author.getCode());
    }

    /**
     * 得到时间降序评论列表
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/19 15:47
     */
    @Override
    public R queryTimeDownSortComment(String userId, Long resourceId, Long currentPage, Integer pageSize) {
        QueryWrapper<ResourceComment> resourceCommentQueryWrapper = new QueryWrapper<>();
        resourceCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        resourceCommentQueryWrapper.orderByAsc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourceCommentMapper.selectPage(page, resourceCommentQueryWrapper);
        List<ResourceComment> resourceCommentList = selectPage.getRecords();
        JSONObject commentDetail = this.getCommentDetail(userId, resourceCommentList);
        Long commentCount = commentDetail.getLong("commentCount");
        List<ResourceComment> articleCommentList =
                JSONObject.parseArray(commentDetail.getString("resourceCommentList"), ResourceComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 得到时间升序评论列表
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/19 15:51
     */
    @Override
    public R queryTimeUpgradeComment(String userId, Long resourceId, Long currentPage, Integer pageSize) {
        QueryWrapper<ResourceComment> resourceCommentQueryWrapper = new QueryWrapper<>();
        resourceCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        resourceCommentQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourceCommentMapper.selectPage(page, resourceCommentQueryWrapper);
        List<ResourceComment> resourceCommentList = selectPage.getRecords();
        JSONObject commentDetail = this.getCommentDetail(userId, resourceCommentList);
        Long commentCount = commentDetail.getLong("commentCount");
        List<ResourceComment> articleCommentList =
                JSONObject.parseArray(commentDetail.getString("resourceCommentList"), ResourceComment.class);
        selectPage.setRecords(articleCommentList);

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 功能描述
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/19 15:52
     */
    @Override
    public R queryNotReplyComment(String userId, Long resourceId, Long currentPage, Integer pageSize) {
        QueryWrapper<ResourceComment> resourceCommentQueryWrapper = new QueryWrapper<>();
        resourceCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        resourceCommentQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourceCommentMapper.selectPage(page, resourceCommentQueryWrapper);
        List<ResourceComment> resourceCommentList = selectPage.getRecords();
        long commentCount= 0;

        ListIterator<ResourceComment> resourceCommentListIterator = resourceCommentList.listIterator();
        while (resourceCommentListIterator.hasNext()) {
            ResourceComment resourceComment = resourceCommentListIterator.next();
            Long oneCommentId = resourceComment.getId();
            // 通过一级评论id得到下面得多级评论
            QueryWrapper<ResourceComment> twoCommentQueryWrapper = new QueryWrapper<>();
            twoCommentQueryWrapper.orderByAsc("create_time");
            twoCommentQueryWrapper.eq("parent_id", oneCommentId);
            List<ResourceComment> twoCommentList = resourceCommentMapper.selectList(twoCommentQueryWrapper);

            // 判断当前登录用户是否回复此评论
            boolean existReply = false;
            if (userId != null && !"".equals(userId)) {
                for (ResourceComment comment : twoCommentList) {
                    Long replyId = comment.getReplyId();
                    if (replyId.equals(Long.parseLong(userId))) {
                        existReply = true;
                    }
                }
            }

            // 如果存在回复删除此评论
            if (existReply) {
                resourceCommentListIterator.remove();
                continue;
            }

            commentCount ++ ;
            // 判断当前登录用户是否点赞一级评论
            if (userId != null && !"".equals(userId)) {
                QueryWrapper<ResourceCommentLike> oneCommentLikeQueryWrapper = new QueryWrapper<>();
                oneCommentLikeQueryWrapper.eq("user_id", userId);
                oneCommentLikeQueryWrapper.eq("resource_comment_id", oneCommentId);
                Long selectCount = resourceCommentLikeMapper.selectCount(oneCommentLikeQueryWrapper);
                if (selectCount > 0) {
                    resourceComment.setIsLike(ResourcesEnum.IS_LIKE.getCode());
                } else {
                    resourceComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode() );
                }
            } else {
                resourceComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
            }

            // 得到发送人姓名头像
            Long senderId = resourceComment.getSenderId();
            User user = userMapper.selectById(senderId);
            resourceComment.setSenderUsername(user.getUsername());
            resourceComment.setSenderHeadImg(user.getPhoto());
            // 得到多级评论详细信息
            for (ResourceComment twoComment : twoCommentList) {
                commentCount ++ ;
                Long twoCommentId = twoComment.getId();
                // 判断当前登录用户是否点赞二级评论
                if (userId != null && !"".equals(userId)) {
                    QueryWrapper<ResourceCommentLike> twoCommentLikeQueryWrapper = new QueryWrapper<>();
                    twoCommentLikeQueryWrapper.eq("resource_comment_id", twoCommentId);
                    twoCommentLikeQueryWrapper.eq("user_id", userId);
                    Long selectCount = resourceCommentLikeMapper.selectCount(twoCommentLikeQueryWrapper);
                    if (selectCount > 0) {
                        twoComment.setIsLike(ResourcesEnum.IS_LIKE.getCode());
                    } else {
                        twoComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
                    }
                } else {
                    twoComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
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

            resourceComment.setResourceCommentList(twoCommentList);
        }

        selectPage.setRecords(resourceCommentList);
        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("selectPage", selectPage);
        return R.ok(data);
    }

    /**
     * 发表评论回复
     *
     * @param senderId           发送者id
     * @param parentId           评论父id
     * @param replyId            回复者id
     * @param replyContent       回复内容
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/19 17:03
     */
    @Override
    public R publishCommentReply(Long senderId, Long parentId, long replyId, String replyContent, Long resourceId) {

        ResourceComment resourceComment = new ResourceComment();
        resourceComment.setReplyId(replyId);
        resourceComment.setSenderId(senderId);
        resourceComment.setCreateTime(new Date());
        resourceComment.setContent(replyContent);
        resourceComment.setParentId(parentId);
        resourceComment.setResourceId(resourceId);
        resourceCommentMapper.insert(resourceComment);
        // 资源评论数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.resourceCommentCountAddOne);
        data.put("resourceId", resourceId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceDirectExchange,
                RabbitmqRoutingName.redisUpdateRouting, message);

        // 插入社区文章消息表
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.replyInsertResourceMessage);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("senderId", replyId);
        jsonObject.put("recipientId", senderId);
        jsonObject.put("replyContent", replyContent);
        Message message1 = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message1);
        return R.ok();
    }

    /**
     * 得到评论详细信息
     *
     * @param userId 当前登录用户id
     * @param resourceCommentList 资源评论集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 21:32
     */

    private JSONObject getCommentDetail(String userId, List<ResourceComment> resourceCommentList) {
        long commentCount= 0;
        for (ResourceComment resourceComment : resourceCommentList) {
            commentCount ++ ;
            Long oneCommentId = resourceComment.getId();
            // 判断当前登录用户是否点赞一级评论
            if (userId != null && !"".equals(userId)) {
                QueryWrapper<ResourceCommentLike> oneResourceCommentLike = new QueryWrapper<>();
                oneResourceCommentLike.eq("user_id", userId);
                oneResourceCommentLike.eq("resource_comment_id", oneCommentId);
                Long selectCount = resourceCommentLikeMapper.selectCount(oneResourceCommentLike);
                if (selectCount > 0) {
                    resourceComment.setIsLike(ResourcesEnum.IS_LIKE.getCode());
                } else {
                    resourceComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode() );
                }
            } else {
                resourceComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
            }

            // 得到发送人姓名头像
            Long senderId = resourceComment.getSenderId();
            User user = userMapper.selectById(senderId);
            resourceComment.setSenderUsername(user.getUsername());
            resourceComment.setSenderHeadImg(user.getPhoto());

            // 通过一级评论id得到下面得多级评论
            QueryWrapper<ResourceComment> twoResourceCommentQueryWrapper = new QueryWrapper<>();
            twoResourceCommentQueryWrapper.orderByAsc("create_time");
            twoResourceCommentQueryWrapper.eq("parent_id", oneCommentId);
            List<ResourceComment> twoCommentList = resourceCommentMapper.selectList(twoResourceCommentQueryWrapper);
            // 得到多级评论详细信息
            for (ResourceComment twoComment : twoCommentList) {
                commentCount ++ ;
                Long twoCommentId = twoComment.getId();
                // 判断当前登录用户是否点赞二级评论
                if (userId != null && !"".equals(userId)) {
                    QueryWrapper<ResourceCommentLike> twoCommentLikeQueryWrapper = new QueryWrapper<>();
                    twoCommentLikeQueryWrapper.eq("resource_comment_id", twoCommentId);
                    twoCommentLikeQueryWrapper.eq("user_id", userId);
                    Long selectCount = resourceCommentLikeMapper.selectCount(twoCommentLikeQueryWrapper);
                    if (selectCount > 0) {
                        twoComment.setIsLike(ResourcesEnum.IS_LIKE.getCode());
                    } else {
                        twoComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
                    }
                } else {
                    twoComment.setIsLike(ResourcesEnum.NOT_LIKE.getCode());
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

            resourceComment.setResourceCommentList(twoCommentList);
        }

        JSONObject data = new JSONObject();
        data.put("commentCount", commentCount);
        data.put("resourceCommentList", JSONObject.toJSONString(resourceCommentList));
        return data;
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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 10:56
     */
    @Transactional
    @Override
    public R deleteComment(Long commentId, Long resourceId) {
        int deleteById = resourceCommentMapper.deleteById(commentId);
        // 删掉所有的子评论
        QueryWrapper<ResourceComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("parent_id", commentId);
        List<ResourceComment> resourceCommentList = resourceCommentMapper.selectList(commentQueryWrapper);
        if (resourceCommentList != null && resourceCommentList.size() > 0) {
            List<Long> commentIdList = new ArrayList<>();
            for (ResourceComment comment : resourceCommentList) {
                Long id = comment.getId();
                commentIdList.add(id);
            }

            QueryWrapper<ResourceComment> resourceCommentQueryWrapper = new QueryWrapper<>();
            resourceCommentQueryWrapper.in("id", commentIdList);
            int delete = resourceCommentMapper.delete(resourceCommentQueryWrapper);

            UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", resourceId);
            updateWrapper.setSql("comment_count = comment_count - " + (delete + deleteById));
            resourcesMapper.update(null, updateWrapper);

            // 删除子评论点赞
            QueryWrapper<ResourceCommentLike> resourceCommentLikeQueryWrapper = new QueryWrapper<>();
            resourceCommentLikeQueryWrapper.in("resource_comment_id", commentIdList);
            resourceCommentLikeMapper.delete(resourceCommentLikeQueryWrapper);
        } else {
            UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", resourceId);
            updateWrapper.setSql("comment_count = comment_count - " + deleteById);
            resourcesMapper.update(null, updateWrapper);
        }

        // 删除评论点赞
        QueryWrapper<ResourceCommentLike> commentLikeQueryWrapper=  new QueryWrapper<>();
        commentLikeQueryWrapper.eq("resource_comment_id", commentId);
        resourceCommentLikeMapper.delete(commentLikeQueryWrapper);


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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);
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
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);
        return R.ok();
    }
}

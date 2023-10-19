package com.monkey.monkeyresource.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.SplitConstant;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.*;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ResourceCommentMapper resourceCommentMapper;
    @Resource
    private ResourceCommentLikeMapper resourceCommentLikeMapper;


    // 资源模块rabbitmq, redis更新队列
    @RabbitListener(queues = RabbitmqQueueName.redisUpdateQueue)
    public void receiverRedisUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块redis更新队列：event ==> {}", event);
            if (EventConstant.redisUpdateClassification.equals(event)) {
                log.info("资源模块更新分类标签");
                this.redisUpdateClassification();
            } else if (EventConstant.updateCreateResourceUserRank.equals(event)) {
                log.info("更新创作资源用户排行");
                this.updateCreateResourceUserRank();
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 资源评论数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 21:16
     */
    private void resourceCommentCountAddOne(Long resourceId) {
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("comment_count = comment_count + 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);
    }

    /**
     * 更新创作资源用户排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 16:50
     */
    private void updateCreateResourceUserRank() {
        List<Resources> resources = resourcesMapper.selectList(null);
        Map<Long, ResourcesVo> resourcesMap = new HashMap<>();
        for (Resources resource : resources) {
            Long userId = resource.getUserId();
            if (!resourcesMap.containsKey(userId)) {
                ResourcesVo resourcesVo = new ResourcesVo();
                BeanUtils.copyProperties(resource, resourcesVo);
                resourcesVo.setResourcesCount(1);
                resourcesMap.put(userId, resourcesVo);
            } else {
                ResourcesVo resourcesVo = resourcesMap.get(userId);
                resourcesVo.setResourcesCount(resourcesVo.getResourcesCount() + 1);
                resourcesVo.setDownCount(resource.getDownCount() + resource.getDownCount());
            }
        }

        List<ResourcesVo> resourcesVoList = new ArrayList<>(resourcesMap.values());

        // 按下载次数, 其次按用户资源量排序
        resourcesVoList.sort((a, b) ->
                {
                    if (a.getDownCount().equals(b.getDownCount())) {
                        return Integer.compare(b.getResourcesCount(), a.getResourcesCount());
                    }
                    return Integer.compare(b.getDownCount(), a.getDownCount());
                });

        // 只保留前 9 条数据
        if (resourcesVoList.size() >= 10) {
            resourcesVoList = resourcesVoList.subList(0, 10);
        }

        // 得到用户信息
        for (ResourcesVo resourcesVo : resourcesVoList) {
            Long userId = resourcesVo.getUserId();
            User user = userMapper.selectById(userId);
            resourcesVo.setUsername(user.getUsername());
            resourcesVo.setHeadImg(user.getPhoto());
        }

        // 存入redis
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.createResourceUserRank, JSONObject.toJSONString(resourcesVoList));
    }


    /**
     * 资源模块更新分类标签
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/12 15:11
     */
    private void redisUpdateClassification() {
        // 查询一级分类标签
        QueryWrapper<ResourceClassification> resourceClassificationQueryWrapper = new QueryWrapper<>();
        resourceClassificationQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        resourceClassificationQueryWrapper.orderByAsc("sort");
        List<ResourceClassification> resourceClassificationList = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);

        // 查询二级分类标签
        for (ResourceClassification resourceClassification : resourceClassificationList) {
            Long resourceClassificationId = resourceClassification.getId();
            QueryWrapper<ResourceClassification> classificationQueryWrapper = new QueryWrapper<>();
            classificationQueryWrapper.eq("parent_id", resourceClassificationId);
            classificationQueryWrapper.orderByAsc("sort");
            List<ResourceClassification> resourceClassifications = resourceClassificationMapper.selectList(classificationQueryWrapper);

            // 存入redis
            String redisKey = RedisKeyConstant.twoClassificationList + resourceClassificationId;
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(resourceClassifications));
        }

        // 将一级标签存入redis
        String redisKey = RedisKeyConstant.oneClassification;
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(resourceClassificationList));
    }

    // 资源模块死信redis更新队列
    @RabbitListener(queues = RabbitmqQueueName.redisUpdateDlxQueue)
    public void receiverRedisDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块redis死信更新队列：event ==> {}", event);

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 资源模块rabbitmq删除队列
    @RabbitListener(queues = RabbitmqQueueName.resourceDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 资源模块rabbitmq死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.resourceDeleteDlxQueue)
    public void receiverDlxDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq死信删除队列：event ==> {}", event);
            
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 资源模块rabbitmq更新队列
    @RabbitListener(queues = RabbitmqQueueName.resourceUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq更新队列：event ==> {}", event);
            if (EventConstant.resourceCommentCountAddOne.equals(event)) {
                log.info("资源评论数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceCommentCountAddOne(resourceId);
            } else if (EventConstant.curationComment.equals(event)) {
                log.info("精选评论");
                Long commentId = data.getLong("commentId");
                this.curationComment(commentId);
            } else if (EventConstant.cancelCurationComment.equals(event)) {
                log.info("取消精选评论");
                Long commentId = data.getLong("commentId");
                this.cancelCurationComment(commentId);
            } else if (EventConstant.topComment.equals(event)) {
                log.info("置顶评论");
                Long commentId = data.getLong("commentId");
                this.topComment(commentId);
            } else if (EventConstant.cancelTopComment.equals(event)) {
                log.info("取消置顶评论");
                Long commentId = data.getLong("commentId");
                this.cancelTopComment(commentId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
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
        UpdateWrapper<ResourceComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_top", ResourcesEnum.COMMENT_NOT_TOP.getCode());
        resourceCommentMapper.update(null, updateWrapper);
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
        UpdateWrapper<ResourceComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_top", ResourcesEnum.COMMENT_IS_TOP.getCode());
        resourceCommentMapper.update(null, updateWrapper);
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
        UpdateWrapper<ResourceComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_curation", ResourcesEnum.COMMENT_NOT_CURATION.getCode());
        resourceCommentMapper.update(null, updateWrapper);
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
        UpdateWrapper<ResourceComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", commentId);
        updateWrapper.set("is_curation", ResourcesEnum.COMMENT_IS_CURATION.getCode());
        resourceCommentMapper.update(null, updateWrapper);
    }


    // 资源模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.resourceUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq死信更新队列：event ==> {}", event);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 资源模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.resourceInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq插入队列：event ==> {}", event);
            if (EventConstant.uploadResource.equals(event)) {
                log.info("上传资源");
                long userId = data.getLong("userId");
                UploadResourcesVo uploadResourcesVo = JSONObject.parseObject(data.getString("uploadResourcesVo"), UploadResourcesVo.class);
                this.uploadResource(userId, uploadResourcesVo);
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
        QueryWrapper<ResourceCommentLike> resourceCommentLikeQueryWrapper = new QueryWrapper<>();
        resourceCommentLikeQueryWrapper.eq("user_id", userId);
        resourceCommentLikeQueryWrapper.eq("resource_comment_id", commentId);
        resourceCommentLikeMapper.delete(resourceCommentLikeQueryWrapper);

        // 点赞数 - 1
        UpdateWrapper<ResourceComment> resourceCommentUpdateWrapper = new UpdateWrapper<>();
        resourceCommentUpdateWrapper.eq("id", commentId);
        resourceCommentUpdateWrapper.setSql("like_count = like_count - 1");
        resourceCommentMapper.update(null, resourceCommentUpdateWrapper);
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
        ResourceCommentLike resourceCommentLike = new ResourceCommentLike();
        resourceCommentLike.setCreateTime(new Date());
        resourceCommentLike.setResourceCommentId(commentId);
        resourceCommentLike.setUserId(userId);
        resourceCommentLikeMapper.insert(resourceCommentLike);

        // 点赞数 - 1
        UpdateWrapper<ResourceComment> resourceCommentUpdateWrapper = new UpdateWrapper<>();
        resourceCommentUpdateWrapper.eq("id", commentId);
        resourceCommentUpdateWrapper.setSql("like_count = like_count + 1");
        resourceCommentMapper.update(null, resourceCommentUpdateWrapper);
    }

    /**
     * 上传资源
     *
     * @param userId 用户id
     * @param uploadResourcesVo 资源Vo实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/10 16:21
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadResource(long userId, UploadResourcesVo uploadResourcesVo) {
        Resources resources = new Resources();
        resources.setDescription(uploadResourcesVo.getDescription());
        resources.setName(uploadResourcesVo.getName());
        resources.setUrl(uploadResourcesVo.getUrl());
        resources.setUserId(userId);
        resources.setCreateTime(new Date());
        resources.setUpdateTime(new Date());

        // 添加资源标签
        StringBuilder res = new StringBuilder();
        List<String> resourceLabelList = uploadResourcesVo.getResourceLabelList();
        for (String s : resourceLabelList) {
            // 去除多余的逗号
            s = s.replaceAll(String.valueOf(SplitConstant.resourceLabelSplit), "");
            s = s.replaceAll(" ", "");
            // 建立二级标签关联
            if (!"".equals(s)) {
                res.append(s).append(",");
            }
        }

        if (res.charAt(res.length() - 1) == SplitConstant.resourceLabelSplit) {
            res.deleteCharAt(res.length() - 1);
        }
        resources.setResourceLabel(res.toString());
        resourcesMapper.insert(resources);


        Long resourcesId = resources.getId();



        // 添加所属分类关联
        List<Long> resourceClassificationList = uploadResourcesVo.getResourceClassification();

        // 建立该资源与二级分类关系
        ResourceConnect oneResourceConnect = new ResourceConnect();
        oneResourceConnect.setResourceId(resourcesId);
        oneResourceConnect.setType(uploadResourcesVo.getType());
        oneResourceConnect.setFormTypeId(uploadResourcesVo.getFormTypeId());
        oneResourceConnect.setResourceClassificationId(resourceClassificationList.get(0));
        oneResourceConnect.setLevel(CommonEnum.LABEL_LEVEL_ONE.getCode());
        resourceConnectMapper.insert(oneResourceConnect);



        // 建立二级分类关系
        ResourceConnect twoResourceConnect = new ResourceConnect();
        twoResourceConnect.setResourceId(resourcesId);
        twoResourceConnect.setType(uploadResourcesVo.getType());
        twoResourceConnect.setFormTypeId(uploadResourcesVo.getFormTypeId());
        twoResourceConnect.setResourceClassificationId(resourceClassificationList.get(1));
        twoResourceConnect.setLevel(CommonEnum.LABEL_LEVEL_TWO.getCode());
        resourceConnectMapper.insert(twoResourceConnect);

        // 判断是否收费
        if (uploadResourcesVo.getFormTypeId().equals(FormTypeEnum.FORM_TYPE_TOLL.getCode())) {
            Integer price = uploadResourcesVo.getPrice();
            ResourceCharge resourceCharge = new ResourceCharge();
            resourceCharge.setResourceId(resourcesId);
            resourceCharge.setCreateTime(new Date());
            resourceCharge.setUpdateTime(new Date());
            resourceCharge.setPrice(Float.valueOf(price));
            resourceChargeMapper.insert(resourceCharge);
        }
    }


    // 资源模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.resourceInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq死信插入队列：event ==> {}", event);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
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
}

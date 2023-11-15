package com.monkey.monkeyresource.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.AliPayTradeStatusEnum;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.SplitConstant;
import com.monkey.monkeyresource.feign.ResourceToSearchFeign;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.*;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.monkeyresource.service.ResourcePayService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.monkey.monkeyUtils.util.DateUtils.DATE_TIME_PATTERN;
import static com.monkey.monkeyUtils.util.DateUtils.stringToDate;

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
    @Resource
    private ResourcePayService resourcePayService;
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderLogMapper orderLogMapper;
    @Resource
    private ResourceScoreMapper resourceScoreMapper;
    @Resource
    private ResourceDownConnectMapper resourceDownConnectMapper;
    @Resource
    private ResourceLikeMapper resourceLikeMapper;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private ResourceToSearchFeign resourceToSearchFeign;

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

    // 资源模块死信redis更新队列
    @RabbitListener(queues = RabbitmqQueueName.redisUpdateDlxQueue)
    public void receiverRedisDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块redis死信更新队列：event ==> {}", event);
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

    // 资源订单延迟队列
    @RabbitListener(queues = RabbitmqQueueName.resourceDelayOrderQueue)
    public void receiverOrderDelayQueue(Message message) {
        log.info("资源订单延迟队列");
        try {
            OrderInformation orderInformation = JSONObject.parseObject(message.getBody(), OrderInformation.class);

            Long orderInformationId = orderInformation.getId();
            log.info("查询一小时后的订单状态 ==> orderInformation = {}", orderInformation);
            String result = resourcePayService.queryAliPayOrder(orderInformationId);
            if (result == null) {
                log.warn("订单未创建: == > {}", orderInformationId);
            } else {
                // 解析支付包的返回结果
                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONObject alipayTradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");
                String tradeStatus = alipayTradeQueryResponse.getString("trade_status");
                if (AliPayTradeStatusEnum.WAIT_BUYER_PAY.getStatus().equals(tradeStatus)) {
                    log.warn("资源模块核实订单未支付 == > {}", orderInformationId);

                    // 未支付，调用支付宝关单接口
                    resourcePayService.closeAliPayOrder(orderInformationId);
                    // 更新订单状态
                    orderInformation.setOrderStatus(CommonEnum.EXCEED_TIME_AlREADY_CLOSE.getMsg());
                    orderInformationMapper.updateById(orderInformation);
                } else if (AliPayTradeStatusEnum.TRADE_SUCCESS.getStatus().equals(tradeStatus)) {
                    // 如果订单已支付，则更新订单状态（可能是因为支付宝支付成功请求发送失败，或内网穿透失败）没有更新订单状态
                    log.info("资源模块核实订单已支付 == > {}", orderInformationId);
                    OrderInformation order = orderInformationMapper.selectById(orderInformationId);
                    String orderStatus = order.getOrderStatus();
                    if (CommonEnum.NOT_PAY_FEE.getMsg().equals(orderStatus)) {
                        // 更新订单接口
                        order.setOrderStatus(CommonEnum.WAIT_EVALUATE.getMsg());
                        orderInformationMapper.updateById(order);

                        // 记录支付日志
                        JSONObject data = new JSONObject();
                        data.put("event", EventConstant.insertPayUpdateFailLog);
                        data.put("alipayTradeQueryResponse", alipayTradeQueryResponse.toJSONString());
                        Message message1 = new Message(data.toJSONString().getBytes());
                        // 新增支付日志
                        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                                RabbitmqRoutingName.resourceInsertRouting, message1);

                        // elasticsearch资源人数 + 1
                        resourceToSearchFeign.resourceBuyCountAddOne(order.getAssociationId());
                    }
                }
            }
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
            if (EventConstant.deleteResourceChildrenComment.equals(event)) {
                log.info("资源模块删除资源评论");
                Long commentId = data.getLong("commentId");
                Long resourceId = data.getLong("resourceId");
                Long deleteById = data.getLong("deleteById");
                this.deleteResourceChildrenComment(commentId, resourceId, deleteById);
            }
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
            if (EventConstant.deleteResourceChildrenComment.equals(event)) {
                log.info("资源模块删除资源评论");
                Long commentId = data.getLong("commentId");
                Long resourceId = data.getLong("resourceId");
                Long deleteById = data.getLong("deleteById");
                this.deleteResourceChildrenComment(commentId, resourceId, deleteById);
            }
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
            } else if (EventConstant.resourceBuyCountAddOne.equals(event)) {
                log.info("资源模块购买资源数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceBuyCountAddOne(resourceId);
            } else if (EventConstant.resourceViewCountAddOne.equals(event)) {
                log.info("资源游览数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceViewCountAddOne(resourceId);
            } else if (EventConstant.curationResource.equals(event)) {
                log.info("精选资源");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.curationResource(userId, resourceId);
            } else if (EventConstant.cancelCurationResource.equals(event)) {
                log.info("取消精选资源");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.cancelCurationResource(userId, resourceId);
            } else if (EventConstant.resourceCollectCountAddOne.equals(event)) {
                log.info("资源收藏数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceCollectCountAddOne(resourceId);
            } else if (EventConstant.resourceCollectCountSubOne.equals(event)) {
                log.info("资源收藏数 - 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceCollectCountSubOne(resourceId);
            } else if (EventConstant.resourceBuyCountSubOne.equals(event)) {
                log.info("资源购买数 - 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceBuyCountSubOne(resourceId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }



    // 资源模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.resourceUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq死信更新队列：event ==> {}", event);
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
            } else if (EventConstant.resourceBuyCountAddOne.equals(event)) {
                log.info("资源模块购买资源数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceBuyCountAddOne(resourceId);
            } else if (EventConstant.resourceViewCountAddOne.equals(event)) {
                log.info("资源游览数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceViewCountAddOne(resourceId);
            } else if (EventConstant.curationResource.equals(event)) {
                log.info("精选资源");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.curationResource(userId, resourceId);
            } else if (EventConstant.cancelCurationResource.equals(event)) {
                log.info("取消精选资源");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.cancelCurationResource(userId, resourceId);
            } else if (EventConstant.resourceCollectCountAddOne.equals(event)) {
                log.info("资源收藏数 + 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceCollectCountAddOne(resourceId);
            } else if (EventConstant.resourceCollectCountSubOne.equals(event)) {
                log.info("资源收藏数 - 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceCollectCountSubOne(resourceId);
            } else if (EventConstant.resourceBuyCountSubOne.equals(event)) {
                log.info("资源购买数 - 1");
                Long resourceId = data.getLong("resourceId");
                this.resourceBuyCountSubOne(resourceId);
            }
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
            } else if (EventConstant.insertPayUpdateFailLog.equals(event)) {
                log.info("插入支付成功更新失败日志");
                JSONObject alipayTradeQueryResponse = JSONObject.parseObject(data.getString("alipayTradeQueryResponse"), JSONObject.class);
                this.insertPayUpdateFailLog(alipayTradeQueryResponse);
            } else if (EventConstant.insertPayUpdateSuccessLog.equals(event)) {
                log.info("插入支付成功更新成功日志");
                HashMap<String, String> hashMap = JSONObject.parseObject(data.getString("data"), new TypeReference<HashMap<String, String>>() {});
                this.insertPayUpdateSuccessLog(hashMap);

            } else if (EventConstant.resourceScore.equals(event)) {
                log.info("资源评分插入队列");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                Integer resourceScore = data.getInteger("resourceScore");
                this.resourceScore(userId, resourceId, resourceScore);
            } else if (EventConstant.insertResourceDown.equals(event)) {
                log.info("插入资源下载表");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.insertResourceDown(userId, resourceId);
            } else if (EventConstant.resourceLike.equals(event)) {
                log.info("资源点赞");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.resourceLike(userId, resourceId);
            } else if (EventConstant.cancelResourceLike.equals(event)) {
                log.info("取消资源点赞");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.cancelResourceLike(userId, resourceId);
            } else if (EventConstant.commentInsertResourceMessage.equals(event)) {
                log.info("评论插入资源消息表");
                Long resourceId = data.getLong("resourceId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.commentInsertResourceMessage(resourceId, senderId, commentContent);
            } else if (EventConstant.replyInsertResourceMessage.equals(event)) {
                log.info("评论回复插入资源消息表");
                Long resourceId = data.getLong("resourceId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.replyInsertResourceMessage(resourceId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertResourceLikeContentMessage.equals(event)) {
                log.info("插入资源消息点赞表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                this.insertLikeContentMessage(associationId, senderId, recipientId);
            } else if (EventConstant.insertResourceLikeCommentMessage.equals(event)) {
                log.info("插入资源评论消息点赞内容表");
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

    // 资源模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.resourceInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("资源模块rabbitmq死信插入队列：event ==> {}", event);
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
            } else if (EventConstant.insertPayUpdateFailLog.equals(event)) {
                log.info("插入支付成功更新失败日志");
                JSONObject alipayTradeQueryResponse = JSONObject.parseObject(data.getString("alipayTradeQueryResponse"), JSONObject.class);
                this.insertPayUpdateFailLog(alipayTradeQueryResponse);
            } else if (EventConstant.insertPayUpdateSuccessLog.equals(event)) {
                log.info("插入支付成功更新成功日志");
                HashMap<String, String> hashMap = JSONObject.parseObject(data.getString("data"), new TypeReference<HashMap<String, String>>() {});
                this.insertPayUpdateSuccessLog(hashMap);

            } else if (EventConstant.resourceScore.equals(event)) {
                log.info("资源评分插入队列");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                Integer resourceScore = data.getInteger("resourceScore");
                this.resourceScore(userId, resourceId, resourceScore);
            } else if (EventConstant.insertResourceDown.equals(event)) {
                log.info("插入资源下载表");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.insertResourceDown(userId, resourceId);
            } else if (EventConstant.resourceLike.equals(event)) {
                log.info("资源点赞");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.resourceLike(userId, resourceId);
            } else if (EventConstant.cancelResourceLike.equals(event)) {
                log.info("取消资源点赞");
                Long userId = data.getLong("userId");
                Long resourceId = data.getLong("resourceId");
                this.cancelResourceLike(userId, resourceId);
            } else if (EventConstant.commentInsertResourceMessage.equals(event)) {
                log.info("评论插入资源消息表");
                Long resourceId = data.getLong("resourceId");
                Long senderId = data.getLong("senderId");
                String commentContent = data.getString("commentContent");
                this.commentInsertResourceMessage(resourceId, senderId, commentContent);
            } else if (EventConstant.replyInsertResourceMessage.equals(event)) {
                log.info("评论回复插入资源消息表");
                Long resourceId = data.getLong("resourceId");
                Long senderId = data.getLong("senderId");
                String replyContent = data.getString("replyContent");
                Long recipientId = data.getLong("recipientId");
                Long commentId = data.getLong("commentId");
                this.replyInsertResourceMessage(resourceId, senderId, recipientId, replyContent, commentId);
            } else if (EventConstant.insertResourceLikeContentMessage.equals(event)) {
                log.info("插入资源消息点赞表");
                Long associationId = data.getLong("associationId");
                Long senderId = data.getLong("senderId");
                Long recipientId = data.getLong("recipientId");
                this.insertLikeContentMessage(associationId, senderId, recipientId);
            } else if (EventConstant.insertResourceLikeCommentMessage.equals(event)) {
                log.info("插入资源评论消息点赞内容表");
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

    /**
     * 删除资源子评论
     *
     * @param commentId 父评论id
     * @param resourceId 资源id
     * @param deleteById 删除的父资源数
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 9:50
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteResourceChildrenComment(Long commentId, Long resourceId, Long deleteById) {
        // 删掉所有的子评论
        QueryWrapper<ResourceComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("parent_id", commentId);
        // 删除评论数
        long deleteCount = 0;
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
            deleteCount = delete + deleteById;
            // 删除子评论点赞
            QueryWrapper<ResourceCommentLike> resourceCommentLikeQueryWrapper = new QueryWrapper<>();
            resourceCommentLikeQueryWrapper.in("resource_comment_id", commentIdList);
            resourceCommentLikeMapper.delete(resourceCommentLikeQueryWrapper);
        } else {
            UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", resourceId);
            updateWrapper.setSql("comment_count = comment_count - " + deleteById);
            deleteCount = deleteById;
            resourcesMapper.update(null, updateWrapper);
        }

        // 删除评论点赞
        QueryWrapper<ResourceCommentLike> commentLikeQueryWrapper=  new QueryWrapper<>();
        commentLikeQueryWrapper.eq("resource_comment_id", commentId);
        resourceCommentLikeMapper.delete(commentLikeQueryWrapper);

        resourceToSearchFeign.resourceCommentSub(resourceId, deleteCount);
    }

    /**
     * 资源购买数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 9:32
     */
    private void resourceBuyCountSubOne(Long resourceId) {
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", resourceId);
        updateWrapper.setSql("buy_count = buy_count - 1");
        resourcesMapper.update(null, updateWrapper);

        resourceToSearchFeign.resourceBuyCountSubOne(resourceId);
    }

    /**
     * 插入文章评论消息点赞内容表
     *
     * @param associationId 文章id
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
        messageLike.setType(MessageEnum.RESOURCE_MESSAGE.getCode());
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
        messageLike.setType(MessageEnum.RESOURCE_MESSAGE.getCode());
        messageLike.setAssociationId(associationId);
        messageLike.setSenderId(senderId);
        messageLike.setIsComment(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLike.setRecipientId(recipientId);
        messageLikeMapper.insert(messageLike);
    }

    /**
     * 评论回复插入资源消息表
     *
     * @param resourceId 资源id
     * @param senderId 发送者id
     * @param replyContent 发送内容
     * @param recipientId 接收者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:56
     */
    private void replyInsertResourceMessage(Long resourceId, Long senderId, Long recipientId, String replyContent, Long commentId) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.RESOURCE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(resourceId);
        messageCommentReply.setSendContent(replyContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_REPLY.getCode());
        messageCommentReply.setRecipientId(recipientId);
        messageCommentReply.setCommentId(commentId);
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 评论插入资源消息表
     *
     * @param resourceId 资源id
     * @param senderId 发送者id
     * @param commentContent 发送内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 16:56
     */
    private void commentInsertResourceMessage(Long resourceId, Long senderId, String commentContent) {
        MessageCommentReply messageCommentReply = new MessageCommentReply();
        messageCommentReply.setCreateTime(new Date());
        messageCommentReply.setType(MessageEnum.RESOURCE_MESSAGE.getCode());
        messageCommentReply.setAssociationId(resourceId);
        messageCommentReply.setSendContent(commentContent);
        messageCommentReply.setSenderId(senderId);
        messageCommentReply.setIsComment(CommonEnum.MESSAGE_IS_COMMENT.getCode());
        Resources resources = resourcesMapper.selectById(resourceId);
        messageCommentReply.setRecipientId(resources.getUserId());
        messageCommentReplyMapper.insert(messageCommentReply);
    }

    /**
     * 取消资源点赞
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 11:58
     */
    private void cancelResourceLike(Long userId, Long resourceId) {
        // 删除资源点赞
        QueryWrapper<ResourceLike> resourceLikeQueryWrapper = new QueryWrapper<>();
        resourceLikeQueryWrapper.eq("user_id", userId);
        resourceLikeQueryWrapper.eq("resource_id", resourceId);
        resourceLikeMapper.delete(resourceLikeQueryWrapper);

        // 资源点赞数 - 1
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("like_count = like_count - 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceLikeCountSubOne(resourceId);
        Resources resources = resourcesMapper.selectById(resourceId);
        resourceToSearchFeign.userLikeCountSubOne(resources.getUserId());
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

        resourceToSearchFeign.resourceCommentCountAdd(resourceId);
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

    /**
     * 资源点赞
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 11:52
     */
    private void resourceLike(Long userId, Long resourceId) {
        // 插入资源点赞表
        ResourceLike resourceLike = new ResourceLike();
        resourceLike.setResourceId(resourceId);
        resourceLike.setUserId(userId);
        resourceLike.setCreateTime(new Date());
        resourceLikeMapper.insert(resourceLike);

        // 资源点赞数 + 1
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("like_count = like_count + 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceLikeCountAddOne(resourceId);
        Resources resources = resourcesMapper.selectById(resourceId);
        resourceToSearchFeign.userLikeCountAddOne(resources.getUserId());
    }


    /**
     * 资源收藏数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:35
     */
    private void resourceCollectCountSubOne(Long resourceId) {
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("collect_count = collect_count - 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceCollectCountSubOne(resourceId);
        Resources resources = resourcesMapper.selectById(resourceId);
        resourceToSearchFeign.userCollectCountSubOne(resources.getUserId());
    }

    /**
     * 资源收藏数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:34
     */
    private void resourceCollectCountAddOne(Long resourceId) {
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("collect_count = collect_count + 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceCollectCountAddOne(resourceId);
        Resources resources = resourcesMapper.selectById(resourceId);
        resourceToSearchFeign.userCollectCountAddOne(resources.getUserId());
    }

    /**
     * 取消精选资源
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 11:51
     */
    private void cancelCurationResource(Long userId, Long resourceId) {
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", resourceId);
        updateWrapper.set("is_curation", ResourcesEnum.NOT_CURATION.getCode());
        resourcesMapper.update(null, updateWrapper);
    }

    /**
     * 精选资源
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 11:48
     */
    private void curationResource(Long userId, Long resourceId) {
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", resourceId);
        updateWrapper.set("is_curation", ResourcesEnum.IS_CURATION.getCode());
        resourcesMapper.update(null, updateWrapper);
    }

    /**
     * 资源游览数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/23 8:06
     */
    private void resourceViewCountAddOne(Long resourceId) {
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("view_count = view_count + 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceViewAddOne(resourceId);
        Resources resources = resourcesMapper.selectById(resourceId);
        resourceToSearchFeign.userViewAddOne(resources.getUserId());
    }

    /**
     * 资源购买数量 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 16:31
     */
    private void resourceBuyCountAddOne(Long resourceId) {
        UpdateWrapper<Resources> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", resourceId);
        updateWrapper.setSql("buy_count = buy_count + 1");
        resourcesMapper.update(null, updateWrapper);

        resourceToSearchFeign.resourceBuyCountAddOne(resourceId);
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


    /**
     * 插入资源下载表
     *
     * @param userId 登录用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 17:31
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertResourceDown(Long userId, Long resourceId){
        ResourceDownConnect resourceDownConnect = new ResourceDownConnect();
        resourceDownConnect.setResourceId(resourceId);
        resourceDownConnect.setUserId(userId);
        resourceDownConnect.setCreateTime(new Date());
        resourceDownConnectMapper.insert(resourceDownConnect);

        // 资源下载数 + 1;
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.setSql("down_count = down_count + 1");
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.resourceDownCountAddOne(resourceId);
    }

    /**
     * 资源评分
     *
     * @param userId 用户id
     * @param score 资源评分
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 15:15
     */
    @Transactional(rollbackFor = Exception.class                                                                                                                                    )
    public void resourceScore(Long userId, Long resourceId, Integer score) {
        // 判断用户是否评分
        QueryWrapper<ResourceScore> resourceScoreQueryWrapper = new QueryWrapper<>();
        resourceScoreQueryWrapper.eq("user_id", userId);
        resourceScoreQueryWrapper.eq("resource_id", resourceId);
        ResourceScore resourceScore = resourceScoreMapper.selectOne(resourceScoreQueryWrapper);
        if (resourceScore == null) {
            // 插入用户评分
            ResourceScore res = new ResourceScore();
            res.setResourceId(resourceId);
            res.setUserId(userId);
            res.setScore(score);
            res.setCreateTime(new Date());
            resourceScoreMapper.insert(res);
        } else {
            UpdateWrapper<ResourceScore> resourceScoreUpdateWrapper = new UpdateWrapper<>();
            resourceScoreUpdateWrapper.eq("resource_id", resourceId);
            resourceScoreUpdateWrapper.eq("user_id", userId);
            resourceScoreUpdateWrapper.set("score", score);
            resourceScoreMapper.update(null, resourceScoreUpdateWrapper);
        }

        // 更新资源表中的评分
        QueryWrapper<ResourceScore> scoreQueryWrapper = new QueryWrapper<>();
        scoreQueryWrapper.eq("resource_id", resourceId);
        scoreQueryWrapper.select("score");
        List<ResourceScore> resourceScoreList = resourceScoreMapper.selectList(scoreQueryWrapper);
        long sum = resourceScoreList.stream().mapToInt(ResourceScore::getScore).sum();
        long cnt = resourceScoreList.size();
        float res = (float) sum / cnt;
        UpdateWrapper<Resources> resourcesUpdateWrapper = new UpdateWrapper<>();
        resourcesUpdateWrapper.eq("id", resourceId);
        resourcesUpdateWrapper.set("score", res);
        resourcesUpdateWrapper.set("score_count", cnt);
        resourcesMapper.update(null, resourcesUpdateWrapper);

        resourceToSearchFeign.updateResourceScore(resourceId, res);
    }

    /**
     * 插入支付成功更新成功日志
     *
     * @param data 数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 16:24
     */
    private void insertPayUpdateSuccessLog(HashMap<String, String> data) {
        String gmtCreate = data.get("gmt_create");
        String gmtPayment = data.get("gmt_payment");
        Date payTime = stringToDate(gmtPayment, DATE_TIME_PATTERN);
        Date createTime = stringToDate(gmtCreate, DATE_TIME_PATTERN);

        String totalAmount = data.get("total_amount");
        String outTradeNo = data.get("out_trade_no");
        String tradeStatus = data.get("trade_status");
        String tradeNo = data.get("trade_no");
        String dataJson = JSONObject.toJSONString(data);
        OrderLog orderLog = new OrderLog();
        orderLog.setCreateTime(createTime);
        orderLog.setPayTime(payTime);

        orderLog.setPayMoney(Float.parseFloat(totalAmount));

        orderLog.setId(Long.parseLong(outTradeNo));

        orderLog.setTradeStatus(tradeStatus);
        orderLog.setTradeType(CommonEnum.COMPUTER_PAY.getMsg());
        orderLog.setPayType(CommonEnum.ALIPAY.getMsg());

        orderLog.setNoticeParams(dataJson);

        orderLog.setTransactionId(tradeNo);

        orderLog.setOrderType(CommonEnum.RESOURCE_ORDER.getMsg());

        orderLogMapper.insert(orderLog);
    }

    /**
     * 插入支付成功更新失败日志
     *
     * @param alipayTradeQueryResponse 阿里云返回结果
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 16:05
     */
    private void insertPayUpdateFailLog(JSONObject alipayTradeQueryResponse) {
        Date sendPayDate = alipayTradeQueryResponse.getDate("send_pay_date");
        String totalAmount = alipayTradeQueryResponse.getString("total_amount");
        String outTradeNo = alipayTradeQueryResponse.getString("out_trade_no");
        String tradeNo = alipayTradeQueryResponse.getString("trade_no");
        String tradeStatus = alipayTradeQueryResponse.getString("trade_status");
        OrderLog orderLog = new OrderLog();
        orderLog.setId(Long.parseLong(outTradeNo));

        orderLog.setTradeStatus(tradeStatus);
        orderLog.setTradeType(CommonEnum.COMPUTER_PAY.getMsg());
        orderLog.setPayType(CommonEnum.ALIPAY.getMsg());

        orderLog.setNoticeParams(alipayTradeQueryResponse.toJSONString());

        orderLog.setTransactionId(tradeNo);
        orderLog.setCreateTime(new Date());
        orderLog.setPayTime(sendPayDate);
        orderLog.setPayMoney(Float.parseFloat(totalAmount));
        orderLog.setOrderType(CommonEnum.RESOURCE_ORDER.getMsg());

        orderLogMapper.insert(orderLog);
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

        // 建立该资源与一级级分类关系
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

//        // 得到资源标签名称
//        resources.setResourceLabelName(resourceLabelList);
//        // 得到资源分类名称
//        QueryWrapper<ResourceClassification> resourceClassificationQueryWrapper = new QueryWrapper<>();
//        resourceClassificationQueryWrapper.in("id", resourceClassificationList);
//        resourceClassificationQueryWrapper.select("name");
//        List<ResourceClassification> classifications = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);
//        List<String> resourceClassificationName = new ArrayList<>(classifications.size());
//        for (ResourceClassification resourceClassification : classifications) {
//            resourceClassificationName.add(resourceClassification.getName());
//        }
//        resources.setTypeUrl(FileTypeEnum.getFileUrlByFileType(uploadResourcesVo.getUrl()).getUrl());
//        resources.setFormTypeName(FormTypeEnum.getFormTypeEnum(uploadResourcesVo.getFormTypeId()).getMsg());
//        resources.setResourceClassificationName(resourceClassificationName);
//
//        // 得到用户信息
//        User user = userMapper.selectById(userId);
//        resources.setUsername(user.getUsername());
//        resources.setUserHeadImg(user.getPhoto());
//        resources.setUserBrief(user.getBrief());
//
//        resourceToSearchFeign.insertResourceIndex(JSONObject.toJSONString(resources));
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

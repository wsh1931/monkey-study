package com.monkey.monkeyresource.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
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
    private ResourceLabelMapper resourceLabelMapper;
    @Resource
    private ResourceClassificationConnectMapper resourceClassificationConnectMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    @Resource
    private UserMapper userMapper;


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
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
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
        resources.setType(uploadResourcesVo.getType());
        resources.setFormTypeId(uploadResourcesVo.getFormTypeId());
        resources.setUserId(userId);
        resources.setCreateTime(new Date());
        resources.setUpdateTime(new Date());
        resourcesMapper.insert(resources);
        Long resourcesId = resources.getId();

        // 添加资源标签
        List<ResourceLabel> resourceLabelList = uploadResourcesVo.getResourceLabelList();
        for (ResourceLabel resourceLabel : resourceLabelList) {
            // 建立二级标签关联
            resourceLabel.setResourceId(resourcesId);
            resourceLabel.setCreateTime(new Date());
            resourceLabelMapper.insert(resourceLabel);
        }

        // 添加所属分类关联
        List<ResourceClassification> resourceClassificationList = uploadResourcesVo.getResourceClassificationList();
        Set<Long> set = new HashSet<>();
        for (ResourceClassification resourceClassification : resourceClassificationList) {
            // 建立二级分类关联
            Long parentId = resourceClassification.getParentId();
            // 找到二级标签对应的所有一级标签
            set.add(parentId);
            ResourceClassificationConnect resourceClassificationConnect = new ResourceClassificationConnect();
            resourceClassificationConnect.setResourceId(resourcesId);
            resourceClassificationConnect.setResourceClassificationId(resourceClassification.getId());
            resourceClassificationConnectMapper.insert(resourceClassificationConnect);
        }

        // 建立该资源与一级分类关系
        for (Long id : set) {
            ResourceClassificationConnect resourceClassificationConnect = new ResourceClassificationConnect();
            resourceClassificationConnect.setResourceId(resourcesId);
            resourceClassificationConnect.setResourceClassificationId(id);
            resourceClassificationConnectMapper.insert(resourceClassificationConnect);
        }

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

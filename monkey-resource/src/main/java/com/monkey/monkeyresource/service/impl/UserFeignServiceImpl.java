package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.mapper.ResourceBuyMapper;
import com.monkey.monkeyresource.pojo.ResourceBuy;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:26
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {

    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 删除用户购买资源记录
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 16:32
     */
    @Override
    public R deleteUserBuyResource(Long userId, Long resourceId) {
        QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
        resourceBuyQueryWrapper.eq("user_id", userId);
        resourceBuyQueryWrapper.eq("resource_id", resourceId);
        int delete = resourceBuyMapper.delete(resourceBuyQueryWrapper);
        return R.ok(delete);
    }

    /**
     * 资源收藏数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountAddOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountAddOne);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 资源收藏数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountSubOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountSubOne);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }
}

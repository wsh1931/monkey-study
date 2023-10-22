package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.mapper.ResourceScoreMapper;
import com.monkey.monkeyresource.pojo.ResourceScore;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.ResourcePayFinishService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/22 11:47
 * @version: 1.0
 * @description:
 */
@Service
public class ResourcePayFinishServiceImpl implements ResourcePayFinishService {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourceScoreMapper resourceScoreMapper;
    /**
     * 提交资源评分
     *
     * @param resourceId 资源id
     * @param userId 当前登录用户id
     * @param resourceScore 资源评分
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 11:49
     */
    @Override
    public R submitResourceScore(Long resourceId, long userId, Integer resourceScore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceScore);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("userId", userId);
        jsonObject.put("resourceScore", resourceScore);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);

        return R.ok();
    }

    /**
     * 查询用户资源评分
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 15:46
     */
    @Override
    public R queryUserResourceScore(long userId, Long resourceId) {
        QueryWrapper<ResourceScore> resourceScoreQueryWrapper = new QueryWrapper<>();
        resourceScoreQueryWrapper.eq("user_id", userId);
        resourceScoreQueryWrapper.eq("resource_id", resourceId);
        resourceScoreQueryWrapper.select("score");
        ResourceScore resourceScore = resourceScoreMapper.selectOne(resourceScoreQueryWrapper);
        return R.ok(resourceScore.getScore());
    }
}

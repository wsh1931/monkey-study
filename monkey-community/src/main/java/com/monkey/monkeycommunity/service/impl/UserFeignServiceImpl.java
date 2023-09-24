package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/24 14:31
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 社区文章收藏数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:33
     */
    @Override
    public void communityArticleCollectAddOne(Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleCollectCountAddOne);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
    }

    /**
     * 社区文章收藏数 - 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:33
     */
    @Override
    public void communityArticleCollectSubOne(Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleCollectCountSubOne);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
    }
}

package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.CreateCommunityService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/2 15:08
 * @version: 1.0
 * @description:
 */
@Service
public class CreateCommunityServiceImpl implements CreateCommunityService {
    @Resource
    private CommunityAttributeMapper communityAttributeMapper;
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 得到社区属性列表
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 15:09
     */
    @Override
    public R queryCommunityAttributeList() {
        QueryWrapper<CommunityAttribute> communityAttributeQueryWrapper = new QueryWrapper<>();
        communityAttributeQueryWrapper.orderByAsc("sort");
        communityAttributeQueryWrapper.orderByAsc("create_time");
        return R.ok(communityAttributeMapper.selectList(communityAttributeQueryWrapper));
    }

    /**
     * 得到社区分类列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 15:25
     */
    @Override
    public R queryCommunityClassificationList() {
        QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
        communityClassificationLabelQueryWrapper.eq("level", CommunityEnum.ONE_LEVEL_LABEL.getCode());
        communityClassificationLabelQueryWrapper.orderByAsc("sort");
        communityClassificationLabelQueryWrapper.orderByAsc("create_time");
        return R.ok(communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper));
    }

    /**
     * 创建社区
     *
     * @param community 社区实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 16:10
     */
    @Override
    @Transactional
    public R createCommunity(Community community) {
        JSONObject data = new JSONObject();
        long userId = Long.parseLong(JwtUtil.getUserId());
        data.put("event", EventConstant.createCommunity);
        data.put("community", JSONObject.toJSONString(community));
        data.put("userId", userId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }
}

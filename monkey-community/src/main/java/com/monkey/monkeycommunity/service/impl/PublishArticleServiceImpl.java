package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityChannelEnum;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.PublishArticleService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: wusihao
 * @date: 2023/9/6 16:34
 * @version: 1.0
 * @description:
 */
@Service
public class PublishArticleServiceImpl implements PublishArticleService {
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 查询社区角色列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/6 16:36
     */
    @Override
    public R queryCommunityRoleList(Long communityId) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper1 = new QueryWrapper<>();
        communityRoleConnectQueryWrapper1.eq("community_id", communityId);
        communityRoleConnectQueryWrapper1.groupBy("role_id");
        communityRoleConnectQueryWrapper1.select("role_id, count(*) as count");
        List<Map<String, Object>> roleIdList = communityUserRoleConnectMapper.selectMaps(communityRoleConnectQueryWrapper1);

        // 最终返回集合
        int sum = 0;
        List<CommunityRole> communityRoleList = new ArrayList<>();
        for (Map<String, Object> temp : roleIdList) {
            long roleId = Long.parseLong(temp.get("role_id").toString());
            CommunityRole communityRole = communityRoleMapper.selectById(roleId);
            int count = Integer.parseInt(temp.get("count").toString());
            communityRole.setCount(count);
            sum += count;


            // 通过角色id查询用户列表
            QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("role_id", roleId);
            communityRoleConnectQueryWrapper.eq("community_id", communityId);
            communityRoleConnectQueryWrapper.select("user_id");
            List<Object> objects = communityUserRoleConnectMapper.selectObjs(communityRoleConnectQueryWrapper);
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("id", objects);
            userQueryWrapper.select("photo", "username", "id");
            List<User> userList = userMapper.selectList(userQueryWrapper);
            communityRole.setUserList(userList);
            communityRoleList.add(communityRole);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sum", sum);
        jsonObject.put("communityRoleList", communityRoleList);
        return R.ok(jsonObject);
    }

    /**
     * 通过社区id查询社区频道列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/7 17:15
     */
    @Override
    public R queryCommunityChannelListByCommunityId(Long communityId) {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_CHANNEL_LIST.getKeyName() + communityId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
        }
        QueryWrapper<CommunityChannel> communityChannelQueryWrapper = new QueryWrapper<>();
        communityChannelQueryWrapper.eq("community_id", communityId);
        communityChannelQueryWrapper.orderByAsc("sort");
        communityChannelQueryWrapper.select("id", "channel_name");
        List<CommunityChannel> data = communityChannelMapper.selectList(communityChannelQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(data));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.COMMUNITY_CHANNEL_LIST.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok(data);
    }

    /**
     * 发布社区文章
     *
     * @param userId 当前登录用户id
     * @param communityId 社区id
     * @param communityArticleVo 发布社区文章实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/8 10:07
     */
    @Override
    @Transactional
    public R publishArticle(Long userId, Long communityId, CommunityArticleVo communityArticleVo) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.publishArticle);
        data.put("userId", userId);
        data.put("communityId", communityId);
        data.put("communityArticleVo", JSONObject.toJSONString(communityArticleVo));
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }

    /**
     * 通过社区id查询除了全部的社区频道列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 9:15
     */
    @Override
    public R queryCommunityChannelListByCommunityIdExceptAll(Long communityId) {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_CHANNEL_LIST.getKeyName() + communityId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            List<CommunityChannel> communityChannels = JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey), CommunityChannel.class);
            for (CommunityChannel communityChannel : communityChannels) {
                if (communityChannel.getChannelName().equals(CommunityChannelEnum.ALL.getChannelName())) {
                    communityChannels.remove(communityChannel);
                    break;
                }
            }
            return R.ok(communityChannels);
        }
        QueryWrapper<CommunityChannel> communityChannelQueryWrapper = new QueryWrapper<>();
        communityChannelQueryWrapper.eq("community_id", communityId);
        communityChannelQueryWrapper.orderByAsc("sort");
        communityChannelQueryWrapper.select("id", "channel_name");
        List<CommunityChannel> data = communityChannelMapper.selectList(communityChannelQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(data));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.COMMUNITY_CHANNEL_LIST.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok(data);
    }
}

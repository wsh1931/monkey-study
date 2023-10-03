package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeycommunity.service.CommunityBaseInfoService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: wusihao
 * @date: 2023/9/12 16:39
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class CommunityBaseInfoServiceImpl implements CommunityBaseInfoService {
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityUserApplicationMapper communityUserApplicationMapper;
    @Resource
    private CommunityUserManageMapper communityUserManageMapper;
    /**
     * 查询社区基本信息
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 16:44
     */
    @Override
    public R queryCommunityBaseInfoByCommunityId(Long communityId) {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_BASE_INFO.getKeyName() + communityId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parse(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        Community community = communityMapper.selectById(communityId);
        String toJSONString = JSONObject.toJSONString(community);
        stringRedisTemplate.opsForValue().set(redisKey, toJSONString);
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.COMMUNITY_BASE_INFO.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok(community);
    }

    /**
     * 得到社区标签列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/13 9:02
     */
    @Override
    public R queryCommunityLabelList(Long communityId) {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_LABEL_LIST.getKeyName() + communityId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
        communityLabelConnectQueryWrapper.eq("community_id", communityId);
        communityLabelConnectQueryWrapper.select("community_classification_label_id");
        List<Object> communityLabelIdList = communityLabelConnectMapper.selectObjs(communityLabelConnectQueryWrapper);

        QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
        communityClassificationLabelQueryWrapper.in("id", communityLabelIdList);
        communityClassificationLabelQueryWrapper.select("id", "name");
        List<CommunityClassificationLabel> communityClassificationLabelList = communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityClassificationLabelList));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.COMMUNITY_LABEL_LIST.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok(communityClassificationLabelList);
    }

    /**
     * 得到社区管理员列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/13 9:21
     */
    @Override
    public R queryCommunityManagerList(Long communityId) {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_MANAGER_LIST.getKeyName() + communityId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String str = stringRedisTemplate.opsForValue().get(redisKey);
            return R.ok(JSONObject.parseArray(str));
        }


        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("community_id", communityId);
        communityUserManageQueryWrapper.select("user_id");
        List<Object> userIdList = communityUserManageMapper.selectObjs(communityUserManageQueryWrapper);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        userQueryWrapper.select("id", "photo", "username");
        List<User> userList = userMapper.selectList(userQueryWrapper);

        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userList));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.COMMUNITY_MANAGER_LIST.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok(userList);
    }

    /**
     * 判断当前登录用户是否是社区管理员
     *
     * @param communityId 社区id
     * @param userId 当前登录用户
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/13 13:23
     */
    @Override
    public R judgeUserIsCommunityManagerAndIsInCommunity(Long communityId, String userId) {
        int isManager = CommunityEnum.NOT_MANAGER.getCode();
        JSONObject jsonObject = new JSONObject();
        int inCommunity;
        int status = 0;
        if (userId == null || "".equals(userId)) {
            isManager = CommunityEnum.NOT_MANAGER.getCode();
            inCommunity = CommunityEnum.NOT_COMMUNITY.getCode();
            jsonObject.put("isManager", isManager);
            jsonObject.put("inCommunity", inCommunity);
            return R.ok(jsonObject);
        }

        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("user_id", userId);
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityRoleConnectQueryWrapper.select("role_id");
        CommunityUserRoleConnect communityUserRoleConnect = communityUserRoleConnectMapper.selectOne(communityRoleConnectQueryWrapper);

        if (communityUserRoleConnect == null) {
            // 说明用户还不在社区
            isManager = CommunityEnum.NOT_MANAGER.getCode();
            inCommunity = CommunityEnum.NOT_COMMUNITY.getCode();

            // 判断用户是否申请加入社区
            QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
            communityUserApplicationQueryWrapper.eq("community_id", communityId);
            communityUserApplicationQueryWrapper.eq("user_id", userId);
            CommunityUserApplication communityUserApplication = communityUserApplicationMapper.selectOne(communityUserApplicationQueryWrapper);
            if (communityUserApplication == null) {
                status = CommunityEnum.NOT_APPLICATION.getCode();
            } else {
                status = communityUserApplication.getStatus();
            }
        } else {
            inCommunity = CommunityEnum.IN_COMMUNITY.getCode();
            // 判断当前登录用户是否为社区管理员
            QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
            communityUserManageQueryWrapper.eq("community_id", communityId);
            communityUserManageQueryWrapper.eq("user_id", userId);
            Long selectCount = communityUserManageMapper.selectCount(communityUserManageQueryWrapper);
            if (selectCount > 0) {
                isManager = CommunityEnum.IS_MANAGER.getCode();
            } else {
                isManager = CommunityEnum.NOT_MANAGER.getCode();
            }
        }


        jsonObject.put("isManager", isManager);
        jsonObject.put("inCommunity", inCommunity);
        jsonObject.put("status", status);
        return R.ok(jsonObject);
    }
}

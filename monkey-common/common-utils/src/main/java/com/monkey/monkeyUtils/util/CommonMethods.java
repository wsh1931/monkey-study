package com.monkey.monkeyUtils.util;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author: wusihao
 * @date: 2023/10/16 14:51
 * @version: 1.0
 * @description: 通用方法
 */
public class CommonMethods {
    /**
     * 通过用户id从redis中得到用户信息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 17:35
     */
    public static User getUserInfoByIdFromRedis(Long userId, StringRedisTemplate stringRedisTemplate) {
        if (userId == null) {
            return null;
        }
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String s = stringRedisTemplate.opsForValue().get(redisKey);
            UserDetailsImpl userDetails = JSONObject.parseObject(s, UserDetailsImpl.class);
            return userDetails.getUser();
        } else {
            return null;
        }
    }

    /**
     * 通过用户id从redis中得到用户信息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 17:35
     */
    public static User getUserInfoByIdFromRedis(String userId, StringRedisTemplate stringRedisTemplate) {
        if (userId == null || "".equals(userId)) {
            return null;
        }
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String s = stringRedisTemplate.opsForValue().get(redisKey);
            UserDetailsImpl userDetails = JSONObject.parseObject(s, UserDetailsImpl.class);
            return userDetails.getUser();
        } else {
            return null;
        }
    }
}

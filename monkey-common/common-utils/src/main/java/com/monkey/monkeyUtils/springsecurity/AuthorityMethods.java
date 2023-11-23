package com.monkey.monkeyUtils.springsecurity;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/22 17:08
 * @version: 1.0
 * @description: 权限常用方法
 */
public class AuthorityMethods {
    /**
     * 实时添加用户权限
     *
     * @param userId 要添加权限的用户id
     * @param perms 要添加的权限字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 17:09
     */
    public static void addUserAuthority(String userId, String perms, StringRedisTemplate stringRedisTemplate) {
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return;
        }

        // 更新用户信息
        UserDetailsImpl userDetails = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(redisKey), UserDetailsImpl.class);
        List<String> permissions = userDetails.getPermissions();
        if (!permissions.contains(perms)) {
            permissions.add(perms);
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
        }
    }

    /**
     * 实时批量添加用户权限
     *
     * @param userId 要添加权限的用户id
     * @param perms 要添加的权限字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 17:09
     */
    public static void batchAddUserAuthority(String userId, List<String> perms, StringRedisTemplate stringRedisTemplate) {
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return;
        }

        // 更新用户信息
        UserDetailsImpl userDetails = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(redisKey), UserDetailsImpl.class);
        List<String> permissions = userDetails.getPermissions();
        perms.stream().forEach(perm -> {
            if (!permissions.contains(perms)) {
                permissions.add(perm);
            }
        });

        userDetails.setPermissions(permissions);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));

    }

    /**
     * 实时删除用户权限
     *
     * @param userId 要删除权限的用户id
     * @param perms 要删除的权限字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 17:09
     */
    public static void deleteUserAuthority(String userId, String perms, StringRedisTemplate stringRedisTemplate) {
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return;
        }

        // 删除用户信息
        UserDetailsImpl userDetails = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(redisKey), UserDetailsImpl.class);
        List<String> permissions = userDetails.getPermissions();
        if (permissions != null && permissions.size() > 0) {
            permissions.remove(perms);
            userDetails.setPermissions(permissions);
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
        }
    }

    /**
     * 实时批量删除用户权限
     *
     * @param userId 要删除权限的用户id
     * @param perms 要删除的权限字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 17:09
     */
    public static void batchDeleteUserAuthority(String userId, List<String> perms, StringRedisTemplate stringRedisTemplate) {
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return;
        }

        // 删除用户信息
        UserDetailsImpl userDetails = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(redisKey), UserDetailsImpl.class);
        List<String> permissions = userDetails.getPermissions();
        perms.stream().forEach(perm -> {
            if (permissions != null && permissions.size() > 0) {
                permissions.remove(perm);
            }
        });

        userDetails.setPermissions(permissions);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
    }

}

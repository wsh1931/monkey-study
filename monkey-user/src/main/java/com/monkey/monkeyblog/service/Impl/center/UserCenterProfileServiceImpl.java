package com.monkey.monkeyblog.service.Impl.center;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import com.monkey.monkeyUtils.util.CommonMethods;
import com.monkey.monkeyblog.constant.TipConstant;
import com.monkey.monkeyblog.service.center.UserCenterProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/2 17:46
 * @version: 1.0
 * @description:
 */
@Service
public class UserCenterProfileServiceImpl implements UserCenterProfileService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserMapper userMapper;

    /**
     * 查询用户信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 20:26
     */
    @Override
    public R queryUserInfo() {
        String userId = JwtUtil.getUserId();
        User userInfoByIdFromRedis = CommonMethods.getUserInfoByIdFromRedis(userId, stringRedisTemplate);
        if (userInfoByIdFromRedis != null) {
            return R.ok(userInfoByIdFromRedis);
        }

        User user = userMapper.selectById(userId);
        user.setPhone(null);
        user.setEmail(null);
        user.setPassword(null);
        return R.ok(user);
    }

    /**
     * 更新用户头像
     *
     * @param photo 用户头像
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 20:49
     */
    @Override
    public R updateUserHeadImg(String photo) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        String userId = JwtUtil.getUserId();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.set(User::getPhoto, photo);
        userMapper.update(null, userLambdaUpdateWrapper);

        // 更新redis中的用户头像
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String s = stringRedisTemplate.opsForValue().get(redisKey);
            UserDetailsImpl userDetails = JSONObject.parseObject(s, UserDetailsImpl.class);
            User user = userDetails.getUser();
            user.setPhoto(photo);
            userDetails.setUser(user);
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
        }
        return R.ok(TipConstant.updateUserHeadImgSuccess);
    }

    /**
     * 更新用户信息
     *
     * @param user 待更新的用户信息实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 21:12
     */
    @Override
    public R updateUserInfo(User user) {
        String userId = JwtUtil.getUserId();
        user.setId(Long.parseLong(userId));
        userMapper.updateById(user);
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String s = stringRedisTemplate.opsForValue().get(redisKey);
            UserDetailsImpl userDetails = JSONObject.parseObject(s, UserDetailsImpl.class);
            User detailsUser = userDetails.getUser();
            BeanUtils.copyProperties(user, detailsUser);
            userDetails.setUser(detailsUser);
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
        }
        return R.ok();
    }
}

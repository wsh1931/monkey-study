package com.monkey.monkeyblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.service.UserHomeFansService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/12/1 11:04
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeFansServiceImpl implements UserHomeFansService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserFansMapper userFansMapper;
    /**
     * 通过用户id查询用户粉丝
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/1 11:07
     */
    @Override
    public R queryUserFansById(String userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<UserFans> userFansLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFansLambdaQueryWrapper.eq(UserFans::getUserId, userId);
        userFansLambdaQueryWrapper.select(UserFans::getFansId);
        userFansLambdaQueryWrapper.orderByDesc(UserFans::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = userFansMapper.selectPage(page, userFansLambdaQueryWrapper);
        List<User> users = new ArrayList<>();
        List<UserFans> userFansList = selectPage.getRecords();
        if (userFansList != null && userFansList.size() > 0) {
            List<Long> userIdList = userFansList.stream().mapToLong(m -> m.getFansId()).boxed().collect(Collectors.toList());

            if (userIdList.size() > 0) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.in(User::getId, userIdList);
                users = userMapper.selectList(userLambdaQueryWrapper);
            }

            // 判断当前登录用户是否关注此用户
            users.forEach(user -> {
                Long id = user.getId();
                String nowUserId = JwtUtil.getUserId();
                LambdaQueryWrapper<UserFans> fansLambdaQueryWrapper = new LambdaQueryWrapper<>();
                fansLambdaQueryWrapper.eq(UserFans::getFansId, nowUserId);
                fansLambdaQueryWrapper.eq(UserFans::getUserId, id);
                Long selectCount = userFansMapper.selectCount(fansLambdaQueryWrapper);
                if (selectCount > 0) {
                    user.setIsFans(CommonEnum.IS_FANS.getCode());
                } else {
                    user.setIsFans(CommonEnum.NOT_FANS.getCode());
                }
            });
        }

        selectPage.setRecords(users);
        return R.ok(selectPage);
    }
}

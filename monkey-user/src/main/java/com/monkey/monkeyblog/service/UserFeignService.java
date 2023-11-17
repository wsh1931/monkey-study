package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.pojo.vo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.pojo.UserFans;

public interface UserFeignService {
    // 通过fans_id和user_id判断当前登录用户是否是对方粉丝
    R judgeLoginUserAndAuthorConnect(long userId, long fansId);

    // 通过用户id得到用户信息
    R getUserInfoByUserId(Long userId);

    // 得到userFans通过userId, 和fansId
    R getUserFansByUserAndAuthorConnect(Long userId, Long fansId);

    // 通过id删除userFans
    R deleteUserFansById(UserFans userFans);

    // 插入userFans
    R addUserFans(UserFansVo userFansVo);

    // // 通过用户id得到用户关注数和粉丝数
    R getUserConcernAndFansCountByUserId(Long userId);
}

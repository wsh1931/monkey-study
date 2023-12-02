package com.monkey.monkeyblog.service.home;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeFansService {
    // 通过用户id查询用户粉丝
    R queryUserFansById(String userId, Long currentPage, Integer pageSize);
}

package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 删除用户购买资源记录
    R deleteUserBuyResource(Long userId, Long resourceId);
}

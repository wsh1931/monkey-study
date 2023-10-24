package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 删除用户购买资源记录
    R deleteUserBuyResource(Long userId, Long resourceId);

    // 资源收藏数 + 1
    R resourceCollectCountAddOne(Long resourceId);

    // 资源收藏数 - 1
    R resourceCollectCountSubOne(Long resourceId);
}

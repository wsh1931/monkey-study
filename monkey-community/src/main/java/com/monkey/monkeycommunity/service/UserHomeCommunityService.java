package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeCommunityService {
    // 通过用户id查询社区集合
    R queryCommunityByUserId(Long userId, Long currentPage, Integer pageSize);

    // 删除社区
    R deleteCommunity(Long communityId);
}

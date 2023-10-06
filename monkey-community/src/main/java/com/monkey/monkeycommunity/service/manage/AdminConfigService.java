package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;

public interface AdminConfigService {
    // 查询社区管理列表
    R queryCommunityManager(Long communityId, Long currentPage, Integer pageSize, String manageIdx);

    // 删除管理员
    R deleteManager(Long communityManageId);


    R addManager(Long communityId, String userId);
}

package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface PublishArticleService {
    // 查询社区角色列表
    R queryCommunityRoleList(Long communityId);

    // 通过社区id查询社区频道列表
    R queryCommunityChannelListByCommunityId(Long communityId);
}

package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;

public interface InfoManageService {

    // 通过社区id查询社区基本信息
    R queryCommunityInfo(Long communityId);

    // 更新社区信息
    R updateCommunityInfo(String communityVoStr);

    // 更新社区公告
    R updateCommunityNotice(Long communityId, String communityNotice);
}

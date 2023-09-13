package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityBaseInfoService {
    // 查询课程基本信息
    R queryCommunityBaseInfoByCommunityId(Long communityId);

    // 得到社区标签列表
    R queryCommunityLabelList(Long communityId);

    // 得到社区管理员列表
    R queryCommunityManagerList(Long communityId);

    // 判断当前登录用户是否是社区管理员
    R judgeUserIsCommunityManagerAndIsInCommunity(Long communityId, String userId);
}

package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;

public interface CreateCommunityService {
    // 得到社区属性列表
    R queryCommunityAttributeList();

    // 得到社区分类列表
    R queryCommunityClassificationList();

    // 创建社区
    R createCommunity(Community community);
}

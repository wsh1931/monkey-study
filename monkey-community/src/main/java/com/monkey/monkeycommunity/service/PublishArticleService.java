package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;

public interface PublishArticleService {
    // 查询社区角色列表
    R queryCommunityRoleList(Long communityId);

    // 通过社区id查询社区频道列表
    R queryCommunityChannelListByCommunityId(Long communityId);

    // 发布社区文章
    R publishArticle(Long userId, Long communityId, CommunityArticleVo communityArticleVo);

    // 通过社区id查询除了全部的社区频道列表
    R queryCommunityChannelListByCommunityIdExceptAll(Long communityId);
}

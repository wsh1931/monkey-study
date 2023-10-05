package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;

public interface ContentManageService {
    // 通过条件查询内容管理集合
    R queryContentManageListByCondition(long nowUserId, Long communityId, String status, String channel, String publisherId, Long currentPage, Integer pageSize);

    // 判断社区文章是否存在
    R judgeCommunityArticleIsExist(Long communityArticleId);
}

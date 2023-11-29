package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticle;

public interface UserHomeCommunityArticleService {
    // 通过用户id查询社区文章集合
    R queryCommunityArticleByUserId(Long userId, Long currentPage, Integer pageSize);



    // 删除社区文章
    R deleteCommunityArticle(Long communityArticleId);
}

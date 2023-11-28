package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticle;

public interface UserHomeCommunityArticleService {
    // 通过用户id查询社区文章集合
    R queryCommunityArticleByUserId(Long userId, Long currentPage, Integer pageSize);

    // 查询社区文章信息
    R queryCommunityArticle(Long communityArticleId);

    // 删除数据库中的图片
    R deleteCommunityArticlePicture(Long communityArticleId);

    // 更新社区文章
    R updateCommunityArticle(CommunityArticle communityArticle);

    // 删除社区文章
    R deleteCommunityArticle(Long communityArticleId);
}

package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticle;

public interface CommunityArticleEditService {
    // 查询社区文章信息
    R queryCommunityArticle(Long communityArticleId);

    // 删除数据库中的图片
    R deleteCommunityArticlePicture(Long communityArticleId);

    // 更新社区文章
    R updateCommunityArticle(CommunityArticle communityArticle);

    // 更新社区文章图片
    R updateCommunityArticlePicture(Long communityArticleId, String picture);
}

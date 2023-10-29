package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 社区文章收藏数 + 1
    void communityArticleCollectAddOne(Long communityArticleId);

    // 社区文章收藏数 - 1
    void communityArticleCollectSubOne(Long communityArticleId);

    // 通过社区文章id得到社区文章信息
    R queryCommunityArticleById(Long communityArticleId);

    // 通过社区文章id和评论id得到社区文章信息
    R queryCommunityArticleAndCommentById(Long communityArticleId, Long commentId);

    // 通过社区文章id得到社区文章作者id
    Long queryCommunityArticleAuthorById(Long communityArticleId);
}

package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Date;

public interface UserFeignService {
    // 社区文章收藏数 + 1
    void communityArticleCollectAddOne(Long communityArticleId);

    // 社区文章收藏数 - 1
    void communityArticleCollectSubOne(Long communityArticleId, Date createTime);

    // 通过社区文章id得到社区文章信息
    R queryCommunityArticleById(Long communityArticleId);

    // 通过社区文章id和评论id得到社区文章信息
    R queryCommunityArticleAndCommentById(Long communityArticleId, Long commentId);

    // 通过社区文章id得到社区文章作者id
    Long queryCommunityArticleAuthorById(Long communityArticleId);

    // 得到社区文章近一周发表数
    R queryCommunityArticleCountRecentlyWeek(String userId);
}

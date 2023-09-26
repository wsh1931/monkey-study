package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityRankService {
    // 查询社区游览数排行
    R queryCommunityViewCount();

    // 查询社区成员数排行
    R queryCommunityMemberCount();

    // 查询社区文章数排行
    R queryCommunityArticleCount();

    // 查询社区点赞数排行
    R queryCommunityLikeCount();

    // 查询社区评论数排行
    R queryCommunityCommentCount();

    // 查询社区评分数排行
    R queryCommunityScoreCount();

    // 查询社区收藏数排行
    R queryCommunityCollectCount();
}

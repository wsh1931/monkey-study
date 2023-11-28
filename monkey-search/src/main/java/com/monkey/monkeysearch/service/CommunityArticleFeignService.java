package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityArticleFeignService {
    // 课程游览数 + 1
    R communityArticleViewAddOne(Long communityArticleId);

    // 课程评论数 + 1
    R communityArticleCommentCountAdd(Long communityArticleId);


    // 课程收藏数 + 1
    R communityArticleCollectCountAddOne(Long communityArticleId);

    // 课程收藏数 - 1
    R communityArticleCollectCountSubOne(Long communityArticleId);

    // 课程学习人数 + 1
    R communityArticleLikeCountAddOne(Long communityArticleId);

    // 课程学习人数 - 1
    R communityArticleLikeCountSubOne(Long communityArticleId);

    // 更新课程评分
    R updateCommunityArticleScore(Long communityArticleId, Float score);

    // 课程评论数减去对应值
    R communityArticleCommentSub(Long communityArticleId, Long sum);

    // deleteCommunityArticle
    R deleteCommunityArticle(String communityArticleId);
}

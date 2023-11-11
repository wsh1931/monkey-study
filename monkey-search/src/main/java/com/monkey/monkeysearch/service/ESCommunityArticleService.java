package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESCommunityArticleService {
    // 将社区文章数据库中所有数据存入elasticsearch社区文章文档中
    R insertCommunityArticleDocument();

    // 查询综合社区文章列表
    R queryComprehensiveCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询评论最多社区文章列表
    R queryCommentCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多社区文章列表
    R queryCollectCommunityArticle(Integer currentPage, Integer pageSize, String keyword);


    // 查询游览数最多社区文章列表
    R queryViewCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热社区文章列表
    R queryHireCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新社区文章列表
    R queryLatestCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询社区文章评分最高的社区文章列表
    R queryScoreCommunityArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞最多社区文章列表
    R queryLikeCommunityArticle(Integer currentPage, Integer pageSize, String keyword);
}

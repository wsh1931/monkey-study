package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

/**
 * @author: wusihao
 * @date: 2023/11/7 16:28
 * @version: 1.0
 * @description:
 */
public interface ESArticleService {
    // 将文章数据库中所有数据存入elasticsearch文章文档中
    R insertArticleDocument();

    // 查询分页文章elasticsearch
    R queryArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询综合文章列表
    R queryComprehensiveArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询回复最多文章列表
    R queryReplyArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多文章列表
    R queryCollectArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞数最多文章列表
    R queryLikeArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询游览数最多文章列表
    R queryViewArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热文章列表
    R queryHireArticle(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新文章列表
    R queryLatestArticle(Integer currentPage, Integer pageSize, String keyword);
}

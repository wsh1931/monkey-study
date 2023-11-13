package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESResourceService {
    // 将资源数据库中所有数据存入elasticsearch资源文档中
    R insertResourceDocument();

    // 查询综合资源列表
    R queryComprehensiveResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询评论最多资源列表
    R queryCommentResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多资源列表
    R queryCollectResource(Integer currentPage, Integer pageSize, String keyword);


    // 查询游览数最多资源列表
    R queryViewResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞数最多资源列表
    R queryLikeResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热资源列表
    R queryHireResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新资源列表
    R queryLatestResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询资源下载最多的资源列表
    R queryDownResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询购买最多最多资源列表
    R queryBuyResource(Integer currentPage, Integer pageSize, String keyword);

    // 查询资源评分最多的资源列表
    R queryScoreResource(Integer currentPage, Integer pageSize, String keyword);
}

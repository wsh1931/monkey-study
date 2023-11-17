package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESAllService {
    // 查询综合全部列表
    R queryComprehensiveAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询评论最多全部列表
    R queryCommentAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多全部列表
    R queryCollectAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞数最多全部列表
    R queryLikeAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询游览数最多全部列表
    R queryViewAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热全部列表
    R queryHireAll(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新全部列表
    R queryLatestAll(Integer currentPage, Integer pageSize, String keyword);
}

package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESCommunityService {
    // 将社区数据库中所有数据存入elasticsearch社区文档中
    R insertCommunityDocument();
    // 将社区数据库中所有数据存入elasticsearch社区文档中
    R queryComprehensiveCommunity(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热社区列表
    R queryHireCommunity(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新社区列表
    R queryLatestCommunity(Integer currentPage, Integer pageSize, String keyword);

    // 查询社区人数最多社区列表
    R queryMemberCommunity(Integer currentPage, Integer pageSize, String keyword);

    // 查询社区文章最多社区列表
    R queryArticleCommunity(Integer currentPage, Integer pageSize, String keyword);

    // 查询所有社区文档
    R queryCommunityDocument();

    // 删除所有社区文档
    R deleteCommunityDocument();
}

package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchCenterService {
    // 查找相关搜索
    R queryRelatedSearch(String keyWord, Integer searchType);

    // 查询该登录用户最近搜索信息
    R queryHistorySearch(String userId);

    // 将搜索信息插入历史搜索
    R insertHistorySearch(String userId, String keyword);
}

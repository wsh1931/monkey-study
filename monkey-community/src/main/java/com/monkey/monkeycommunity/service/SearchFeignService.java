package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {
    // 查询所有社区文章数据
    R queryAllCommunityArticle();

    // 查询所有社区数据
    R queryAllCommunity();
}

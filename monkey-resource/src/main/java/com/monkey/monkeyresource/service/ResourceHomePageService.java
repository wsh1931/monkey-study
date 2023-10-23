package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface ResourceHomePageService {
    // 查询全部精选资源
    R queryAllCurationResource();

    // 通过选则标签id得到精选资源
    R selectCurationResource(Long classificationId);

    // 查询全部下载次数最多资源
    R queryAllHottestResource();

    // 通过选则标签id得到下载次数最多资源
    R selectHottestResource(Long classificationId);

    // 查询最新资源集合
    R queryLatestResource();

    // 查询资源创作用户排行
    R queryUserRank();

    // 资源游览数 + 1
    R resourceViewCountAddOne(Long resourceId);
}

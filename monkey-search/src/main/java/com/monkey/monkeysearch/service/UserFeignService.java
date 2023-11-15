package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 用户游览数 + 1
    R userViewAddOne(Long userId);

    // 用户作品数 + 1
    R userOpusCountAddOne(Long userId);

    // 用户作品数 - 1
    R userOpusCountSubOne(Long userId);

    // 用户点赞数 + 1
    R userLikeCountAddOne(Long userId);

    // 用户点赞数 - 1
    R userLikeCountSubOne(Long userId);

    // 用户收藏数 + 1
    R userCollectCountAddOne(Long userId);

    // 用户收藏数 - 1
    R userCollectCountSubOne(Long userId);
}

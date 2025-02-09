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

    // 用户粉丝数 + 1
    R userFansCountAddOne(Long userId);

    // 用户粉丝数 - 1
    R userFansCountSubOne(Long userId);

    // 得到作者信息
    R getAuthorInfoById(Long authorId);

    // 查询用户原文数，游览数，点赞数，收藏数
    R queryUserAchievement();
}

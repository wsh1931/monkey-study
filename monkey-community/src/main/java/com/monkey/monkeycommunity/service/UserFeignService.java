package com.monkey.monkeycommunity.service;

public interface UserFeignService {
    // 社区文章收藏数 + 1
    void communityArticleCollectAddOne(Long communityArticleId);

    // 社区文章收藏数 - 1
    void communityArticleCollectSubOne(Long communityArticleId);
}

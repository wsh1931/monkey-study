package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityDetailCardService {
    // 判断是否有显示隐藏框的权力
    R judgePower(Long communityId, String userId);

    // 删除社区文章
    R deleteArticle(Long articleId, Long communityId);

    // 将文章设置为精选内容
    R setExcellentArticle(Long articleId);

    // 将文章设置为置顶内容
    R setTopArticle(Long articleId);

    // 取消文章精选内容
    R cancelExcellentArticle(Long articleId);

    // 取消文章置顶
    R cancelTopArticle(Long articleId);
}

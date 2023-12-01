package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESArticleIndex;

public interface ArticleFeignService {
    // 文章游览数 + 1
    R articleViewAddOne(Long articleId);

    // 文章评论数 + 1
    R articleCommentCountAdd(Long articleId);

    // 文章点赞数 + 1
    R articleLikeCountAddOne(Long articleId);

    // 文章点赞数 - 1
    R articleLikeCountSubOne(Long articleId);

    // 文章收藏数 + 1
    R articleCollectCountAddOne(Long articleId);

    // 文章收藏数 - 1
    R articleCollectCountSubOne(Long articleId);

    // 更新文章实体类
    R updateArticle(ESArticleIndex esArticleIndex);

    // 删除elasticsearch文章
    R deleteArticle(String articleId);
}

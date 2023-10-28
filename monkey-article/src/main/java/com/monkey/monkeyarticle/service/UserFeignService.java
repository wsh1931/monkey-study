package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.pojo.Article;

public interface UserFeignService {
    // 通过用户id得到用户发表文章信息
    R getUserArticleCountByUserId(Long userId);

    // 通过文章id得到文章点赞数
    R getArticleLikeCountByArticleId(Long articleId);

    // 通过文章id得到文章评论数
    R getArticleCommentCountByArticleId(Long articleId);

    // 通过标签id得到文章标签列表
    R getArticleLabelListByLabelId(Long labelId);

    // 通过文章id得到文章标签列表
    R getArticleLabelListByarticleId(Long articleId);

    // 通过用户id得到文章分页列表
    R getArticleListByUserId(Long currentPage, Long pageSize, Long labelId, String userId);


    // 更新文章信息, 文章游览数 + 1
    R updateArticleInfo(Long articleId);

    // 更新文章信息, 文章游览数 - 1
    R subUpdateArticleInfo(Long articleId);

    // 通过文章id得到文章信息
    R queryArticleById(Long articleId);

    // 通过文章id和评论id得到文章信息
    R queryArticleAndCommentById(Long articleId, Long commentId);
}

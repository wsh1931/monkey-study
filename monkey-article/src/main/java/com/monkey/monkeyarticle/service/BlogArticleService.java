package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface BlogArticleService {
    // 通过标签id得到文章内容
    ResultVO getArticleContentByLabelId(String labelId);

    // 博客主页得到所有文章以及分页功能实现
    ResultVO getArticlePagination(Integer currentPage, Integer pageSize, Long labelId, String userId);

    // 得到最近热帖
    ResultVO getRecentlyFireArticle();

    // 用户点赞
    ResultVO userClickPraise(Long articleId, Long userId);

    // 用户取消点赞
    ResultVO userClickOppose(Long articleId, Long userId);

    // 通过文章id得到文章信息
    ResultVO getArticleInformationByArticleId(Long articleId, String userId);

    // 按排序字段得到文章列表
    ResultVO getArticleListBySort();
}

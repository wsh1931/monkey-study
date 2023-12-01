package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeArticleService {
    // 通过用户id查询文章集合
    R queryArticleByUserId(Long userId, Long currentPage, Integer pageSize);

    // 删除文章
    R deleteArticle(String articleId);
}

package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {
    // 查询所有文章
    R queryAllArticle();

    // 得到该用户所有文章，点赞，收藏，游览数
    R queryAllUserArticleInfo();
}

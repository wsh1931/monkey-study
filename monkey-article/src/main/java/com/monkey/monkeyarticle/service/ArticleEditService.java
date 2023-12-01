package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.pojo.Article;

public interface ArticleEditService {
    // 通过文章id查询文章信息
    R queryArticleInfoById(Long articleId);

    // 删除数据库中的图片
    R deleteArticlePicture(Long articleId);

    // 上传图片
    R uploadArticlePicture(String photo, Long articleId);

    // 更新文章
    R updateArticle(Article article);


}

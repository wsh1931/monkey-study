package com.monkey.monkeyblog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyblog.pojo.article.ArticleCommentLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentLikeMapper extends BaseMapper<ArticleCommentLike> {
}

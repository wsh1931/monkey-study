package com.monkey.monkeyarticle.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyarticle.pojo.article.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}

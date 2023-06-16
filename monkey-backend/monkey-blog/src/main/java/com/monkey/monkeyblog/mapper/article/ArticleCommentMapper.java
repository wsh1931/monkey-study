package com.monkey.monkeyblog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyblog.pojo.article.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}

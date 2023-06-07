package com.monkey.monkeyblog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyblog.pojo.article.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

package com.monkey.monkeybackend.Mapper.Blog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeybackend.Pojo.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

package com.monkey.monkeyblog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyblog.pojo.article.ArticleLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {
}

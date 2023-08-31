package com.monkey.monkeycommunity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticleComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区文章评论表(community_article_comment)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface CommunityArticleCommentMapper extends BaseMapper<CommunityArticleComment> {
	
}

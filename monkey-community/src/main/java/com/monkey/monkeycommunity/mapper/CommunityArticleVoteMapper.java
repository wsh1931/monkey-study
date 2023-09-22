package com.monkey.monkeycommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticleVote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区文章投票表(community_article_vote)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface CommunityArticleVoteMapper extends BaseMapper<CommunityArticleVote> {
	
}

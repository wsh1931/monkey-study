package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章点赞表(community_article_like)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article_like")
public class CommunityArticleLike {

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 点赞用户id
	 */
	private Long userId;
	/**
	 * 社区文章id
	 */
	private Long communityArticleId;
	/**
	 * 创造时间
	 */
	private Date createTime;

}

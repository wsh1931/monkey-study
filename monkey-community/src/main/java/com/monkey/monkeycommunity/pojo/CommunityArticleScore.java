package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章评分表(community_article_score)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article_score")
public class CommunityArticleScore implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long  id;
	/**
	 * 收藏用户id
	 */
	private Long userId;
	/**
	 * 社区文章id
	 */
	private Long communityArticleId;
	/**
	 * 分数（1~5）
	 */
	private Integer score;
	/**
	 * 评分时间
	 */
	private Date createTime;

}

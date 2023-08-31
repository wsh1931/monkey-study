package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章表(community_article)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article")
public class CommunityArticle implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 社区id
	 */
	private Long communityId;
	/**
	 * 频道id
	 */
	private Long channelId;
	/**
	 * title
	 */
	private String title;
	/**
	 * 文章内容
	 */
	private String content;
	/**
	 * 文章头像
	 */
	private String picture;
	/**
	 * 是否以任务形式发布（0不是，1是）
	 */
	private Integer isTask;
	/**
	 * 是否已投票形式发布（0不是，1是）
	 */
	private Integer isVote;
	/**
	 * 文章点赞数
	 */
	private Integer likeCount;
	/**
	 * 文章游览数
	 */
	private Integer viewCount;
	/**
	 * 文章收藏数
	 */
	private Integer collectCount;
	/**
	 * 文章评分
	 */
	private Float score;
	/**
	 * 评分人数
	 */
	private Integer scoreCount;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}

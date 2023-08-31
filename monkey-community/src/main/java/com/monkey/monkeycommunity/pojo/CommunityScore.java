package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区积分表(community_score)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_score")
public class CommunityScore {

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 发布内容获得的积分数
	 */
	private Integer publishContent;
	/**
	 * 内容被点赞获得的积分数
	 */
	private Integer likeContent;
	/**
	 * 当日所能获得的最多积分
	 */
	private Integer mostScore;
	/**
	 * 发布评论所获得的积分数
	 */
	private Integer publishComment;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 
	 */
	private Date updateTime;

}

package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import lombok.Data;

/**
 * 社区文章投票表(community_article_vote)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticleVote {

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 社区文章id
	 */
	private Long communityArticleId;
	/**
	 * 
	 */
	private String voteName;
	/**
	 * 投票截止时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date voteDuration;
	/**
	 * 投票种类（0表示单选，1表示多选）
	 */
	private Integer voteKind;
	/**
	 * 已投票人数
	 */
	private Integer votePeople;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

	@TableField(exist = false)
	private List<CommunityArticleVoteItem> communityArticleVoteItemList = new ArrayList<>();
	// 投票是否过期
	@TableField(exist = false)
	private Integer isOverdue;

	// 当前登录用户是否投票
	@TableField(exist = false)
	private Integer isVote = CommunityEnum.NOT_PARTICIPATE_VOTE.getCode();
}

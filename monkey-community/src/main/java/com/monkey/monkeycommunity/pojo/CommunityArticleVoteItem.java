package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import lombok.Data;

/**
 * 社区文章投票选项表(community_article_vote_item)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticleVoteItem {
	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	private Long communityArticleVoteId;
	private String voteContent;
	/**
	 * 票数
	 */
	private Integer poll;
	/**
	 * 投票时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;

	@TableField(exist = false)
	private String username;
	@TableField(exist = false)
	private String headImg;
	@TableField(exist = false)
	private Integer isSelected = CommonEnum.NOT_SELECTED.getCode();
}

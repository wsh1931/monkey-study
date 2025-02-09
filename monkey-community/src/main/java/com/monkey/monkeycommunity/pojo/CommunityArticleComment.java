package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import lombok.Data;

/**
 * 社区文章评论表(community_article_comment)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticleComment {

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 发送人id
	 */
	private Long senderId;
	/**
	 * 回复人id
	 */
	private Long replyId;
	/**
	 * 社区文章表id
	 */
	private Long communityArticleId;
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 父id(0表示一级评论)
	 */
	private Long parentId;
	/**
	 * 是否精选(0表示不精选，1表示精选)
	 */
	private Integer isCuration;

	private Integer isTop;
	/**
	 * 评论点赞数
	 */
	private Long likeCount;
	/**
	 * 评论时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;

	@TableField(exist = false)
	private List<CommunityArticleComment> communityArticleCommentList = new ArrayList<>();

	@TableField(exist = false)
	private Integer isLike;

	@TableField(exist = false)
	private String senderUsername;

	@TableField(exist = false)
	private String senderHeadImg;

	@TableField(exist = false)
	private String replyUsername;

	@TableField(exist = false)
	private String replyHeadImg;

	@TableField(exist = false)
	private Integer isSelected = CommunityEnum.NOT_SELECTED.getCode();
	@TableField(exist = false)
	private Integer isMoreHoverDetail = CommunityEnum.NOT_HOVER.getCode();

	@TableField(exist = false)
	private Integer isMoreHover = CommunityEnum.NOT_HOVER.getCode();

	@TableField(exist = false)
	private String replyContent;

	@TableField(exist = false)
	private Integer isKeyDown = CommunityEnum.NOT_KEYDOWN.getCode();
}

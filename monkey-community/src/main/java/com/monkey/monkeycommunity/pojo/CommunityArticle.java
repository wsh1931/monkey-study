package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import lombok.Data;

/**
 * 社区文章表(community_article)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticle {


	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
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
	private String brief;
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
	private	Integer status;
	private Integer isExcellent;
	private Integer isTop;
	private Integer commentCount;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;

	private Long updateUser;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

	@TableField(exist = false)
	private String username;
	@TableField(exist = false)
	private String userHeadImg;
	@TableField(exist = false)
	private String userBrief;
	@TableField(exist = false)
	private String communityName;
	@TableField(exist = false)
	private String channelName;

	@TableField(exist = false)
	private Integer isHover = CommunityEnum.NOT_HOVER.getCode();
	@TableField(exist = false)
	private Integer isMoreHover = CommunityEnum.NOT_HOVER.getCode();
}

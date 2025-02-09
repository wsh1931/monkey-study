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
import com.monkey.monkeycommunity.constant.CommunityEnum;
import lombok.Data;

/**
 * 社区表(community)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class Community{
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	private Long userId;
	/**
	 * 社区名称
	 */
	private String name;
	/**
	 * 社区描述
	 */
	private String description;
	/**
	 * 社区头像
	 */
	private String photo;
	/**
	 * 社区分类
	 */
	private Long classificationId;
	/**
	 * 评论前是否需要加入社区（0不需要，1需要）
	 */
	private Integer isComment;
	/**
	 * 属性标签
	 */
	private Long attributeLabelId;

	/**
	 * 社区公告
	 */
	private String notice;
	/**
	 * 加入社区方式(0表示无限制，1表示管理员邀请，2表示定向邀请)
	 */
	private Integer enterWay;
	/**
	 * 所有人是否都能看见（0表示可以，1表示只有社区内成员看见）
	 */
	private Long articleCount;
	private Integer isRecommend;
	private Long memberCount;
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
	private List<CommunityClassificationLabel> communityClassificationLabelList = new ArrayList<>();

	@TableField(exist = false)
	private Integer isAdd;

	@TableField(exist = false)
	private Long viewCount = 0L;

	@TableField(exist = false)
	private Long likeCount = 0L;

	@TableField(exist = false)
	private Long commentCount = 0L;

	@TableField(exist = false)
	private Long scoreCount = 0L;

	@TableField(exist = false)
	private Long collectCount = 0L;

	@TableField(exist = false)
	private String username;

	@TableField(exist = false)
	private String headImg;
	@TableField(exist = false)
	private Integer isManager;
	@TableField(exist = false)
	private String classificationName;
	@TableField(exist = false)
	private String attributeLabelName;
	@TableField(exist = false)
	private List<String> contentLabelName = new ArrayList<>();
}

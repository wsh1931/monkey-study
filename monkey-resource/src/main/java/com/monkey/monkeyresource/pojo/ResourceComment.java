package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.monkey.monkeyresource.constant.ResourcesEnum;
import lombok.Data;

/**
 * 资源评论表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_comment")
public class ResourceComment {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 资源id
	 */
	private Long resourceId;
	/**
	 * 发送者id
	 */
	private Long senderId;
	/**
	 * 回复人id
	 */
	private Long replyId;
	/**
	 * 回复内容
	 */
	private String content;
	/**
	 * 父评论id
	 */
	private Long parentId;
	/**
	 * 是否精选(0表示不精选，1表示精选)
	 */
	private Integer isCuration;
	/**
	 * 是否置顶（0表示不置顶，1置顶）
	 */
	private Integer isTop;
	/**
	 * 点赞数
	 */
	private Integer likeCount;
	/**
	 * 评论时间
	 */
	private Date createTime;

	@TableField(exist = false)
	private List<ResourceComment> resourceCommentList = new ArrayList<>();

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
	private Integer isSelected = ResourcesEnum.NOT_SELECTED.getCode();
	@TableField(exist = false)
	private Integer isMoreHoverDetail = ResourcesEnum.NOT_HOVER.getCode();

	@TableField(exist = false)
	private Integer isMoreHover = ResourcesEnum.NOT_HOVER.getCode();

	@TableField(exist = false)
	private String replyContent;

	@TableField(exist = false)
	private Integer isKeyDown = ResourcesEnum.NOT_KEYDOWN.getCode();

}

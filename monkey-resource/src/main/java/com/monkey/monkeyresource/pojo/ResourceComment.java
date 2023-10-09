package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
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
public class ResourceComment implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
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

}

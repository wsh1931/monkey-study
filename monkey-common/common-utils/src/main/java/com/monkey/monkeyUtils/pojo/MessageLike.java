package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消息点赞表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-25 16:47:52
 */
@Data
public class MessageLike {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 发送人id
	 */
	private Long senderId;
	/**
	 * 接收人id
	 */
	private Long recipientId;
	/**
	 * 关联id(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
	 */
	private Long associationId;

	private Long commentId;
	/**
	 * 评论类型(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
	 */
	private Integer type;
	/**
	 * 是否已读消息(0表示未读，1表示已读)
	 */
	private Integer isRead;

	private Integer isComment;
	/**
	 * 创建时间
	 */
	private Date createTime;

}

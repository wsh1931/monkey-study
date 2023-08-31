package com.monkey.monkeyblog.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户消息表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("user_message")
public class UserMessage{

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 发送者id
	 */
	private Long senderId;
	/**
	 * 接收者id
	 */
	private Long receiverId;
	/**
	 * 消息id(当消息类型等于0代表用户评论表id, 1代表新增粉丝表id, 2代表点赞表id, 3代表收藏表id, 4代表私信表id, 5代表群聊id)
	 */
	private Long messageId;
	/**
	 * 消息类型（0代表文章，1代表问答，2代表课程）
	 */
	private Integer messageType;
	/**
	 * 消息详细类型（0代表用户评论表, 1代表新增粉丝表, 2代表点赞表, 3代表收藏表, 4代表私信表, 5代表群聊)）
	 */
	private Integer messageDetailType;
	/**
	 * 创建时间
	 */
	private Date createTime;

}

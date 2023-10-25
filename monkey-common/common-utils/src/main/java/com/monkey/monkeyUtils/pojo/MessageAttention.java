package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import lombok.Data;

/**
 * 消息关注表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-25 16:47:52
 */
@Data
public class MessageAttention{

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
	 * 是否已读消息(0表示未读，1表示已读)
	 */
	private Integer isRead;
	/**
	 * 创建时间
	 */
	private Date createTime;

}

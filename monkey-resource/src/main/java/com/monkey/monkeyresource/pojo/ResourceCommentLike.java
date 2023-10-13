package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源评论点赞表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_comment_like")
public class ResourceCommentLike{

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 资源评论id
	 */
	private Long resourceCommentId;
	/**
	 * 点赞用户id
	 */
	private Long userId;
	/**
	 * 点赞时间
	 */
	private Date createTime;

}

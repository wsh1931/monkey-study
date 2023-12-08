package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 历史点赞表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-12-06 17:24:07
 */
@Data
public class HistoryLike {
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId;
	/**
	 * 作者id
	 */
	private Long authorId;
	/**
	 * 点赞类型(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
	 */
	private Integer type;
	/**
	 * 关联id(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
	 */
	private Long associateId;
	/**
	 * 游览时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;

}

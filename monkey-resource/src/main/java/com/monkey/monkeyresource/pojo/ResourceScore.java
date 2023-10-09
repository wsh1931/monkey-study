package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源评分表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_score")
public class ResourceScore implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 评分人id
	 */
	private Long userId;
	/**
	 * 资源评分
	 */
	private Integer score;
	/**
	 * 评分时间
	 */
	private Date createTime;

}

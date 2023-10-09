package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源标签表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_label")
public class ResourceLabel implements Serializable {
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
	 * 标签名称
	 */
	private String name;
	/**
	 * 创建时间
	 */
	private Date createTime;

}

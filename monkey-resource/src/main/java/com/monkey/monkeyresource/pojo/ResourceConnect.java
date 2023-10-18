package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 资源分类关系表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_connect")
public class ResourceConnect {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 形式类型id
	 */
	private Long formTypeId;

	/**
	 * 资源类型
	 */
	private String type;
	/**
	 * 资源id
	 */
	private Long resourceId;
	/**
	 * 资源分类id
	 */
	private Long resourceClassificationId;

	private Integer level;

	private Integer status;

}

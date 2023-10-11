package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.monkey.monkeyUtils.constants.CommonEnum;
import lombok.Data;

/**
 * 资源分类表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_classification")
public class ResourceClassification implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父id(0表示一级分类)
	 */
	private Long parentId;
	/**
	 * 分类层次(1最顶级标签，依次往下)
	 */
	private Integer level;
	/**
	 * 排序字段
	 */
	private Integer sort;
	/**
	 * 创建时间
	 */
	private Date createTime;

	@TableField(exist = false)
	private Integer selected = CommonEnum.NOT_SELECTED.getCode();
}

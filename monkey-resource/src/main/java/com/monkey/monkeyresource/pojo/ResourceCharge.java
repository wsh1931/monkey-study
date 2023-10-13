package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源收费表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_charge")
public class ResourceCharge {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 资源id
	 */
	private Long resourceId;
	/**
	 * 资源价格
	 */
	private Float price;
	/**
	 * 是否打折（0表示不打折，1表示打折）
	 */
	private Integer isDiscount;
	/**
	 * 折扣
	 */
	private Float discount;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}

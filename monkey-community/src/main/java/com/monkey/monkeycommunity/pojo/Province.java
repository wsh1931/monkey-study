package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 地区表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class Province {

	/**
	 * 自增编号
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 省名称
	 */
	private String name;


}

package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 地区表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("districts")
public class Districts {

	/**
	 * 自增编号
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 父级编号
	 */
	private Integer parentId;
	/**
	 * 行政名称
	 */
	private String name;
	/**
	 * 行政级别
	 */
	private String level;
	/**
	 * 行政编码
	 */
	private String adcode;
	/**
	 * 经纬度
	 */
	private String center;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;
	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

}

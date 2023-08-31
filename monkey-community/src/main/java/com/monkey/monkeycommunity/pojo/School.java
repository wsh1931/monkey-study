package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("school")
public class School implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String address;
	/**
	 * 经度
	 */
	private Float longitude;
	/**
	 * 维度
	 */
	private Float latitude;
	/**
	 * 省
	 */
	private String provinceName;
	/**
	 * 市
	 */
	private String cityName;
	/**
	 * 区
	 */
	private String districtName;
	/**
	 * 
	 */
	private Date createTime;

}

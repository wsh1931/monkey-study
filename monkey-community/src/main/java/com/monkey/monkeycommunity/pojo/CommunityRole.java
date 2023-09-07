package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.spring_security.pojo.User;
import lombok.Data;

/**
 * 社区角色表(community_role)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityRole {

	/**
	 * 角色id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 角色名称
	 */
	private String roleName;
	/**
	 * 晋级条件
	 */
	private String promotionCondition;
	/**
	 * 相关利益
	 */
	private String relatedBenefit;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

	// roleId在该社区中出现的次数
	@TableField(exist = false)
	private Integer count;

	@TableField(exist = false)
	private Boolean selected = false;

	@TableField(exist = false)
	private Boolean allSelected = false;

	@TableField(exist = false)
	private Integer selectedSum = 0;

	@TableField(exist = false)
	private List<User> userList = new ArrayList<>();
}

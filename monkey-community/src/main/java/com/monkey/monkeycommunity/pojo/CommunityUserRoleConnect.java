package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 社区角色关系表(community_role_connect)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityUserRoleConnect {

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 社区id
	 */
	private Long communityId;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 创造时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

}

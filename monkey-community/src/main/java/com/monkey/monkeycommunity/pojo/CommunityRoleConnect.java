package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区角色关系表(community_role_connect)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_role_connect")
public class CommunityRoleConnect {

	/**
	 * 
	 */
	@TableId
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
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date udpateTime;

}

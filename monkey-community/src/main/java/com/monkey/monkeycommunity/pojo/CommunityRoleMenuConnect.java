package com.monkey.monkeycommunity.pojo;

import lombok.Data;

/**
 * 社区角色权限关系表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-11-20 16:24:05
 */
@Data
public class CommunityRoleMenuConnect {
	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 权限id
	 */
	private Long menuId;

}

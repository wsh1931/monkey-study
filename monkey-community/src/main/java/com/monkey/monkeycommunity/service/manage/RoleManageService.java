package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityRole;

public interface RoleManageService {
    // 查询角色管理列表
    R queryRoleManageList(Long communityId, Long currentPage, Integer pageSize);

    // 保存下设头衔
    R preserveDownName(Long roleId, String downName);

    // 提交编辑角色信息
    R submitEditRole(CommunityRole communityRole);

    // 删除下设头衔
    R deleteDownName(String downName, Long roleId);

    // 删除用户角色
    R deleteRole(Long roleId, Long communityId);

    // 新增社区角色
    R addRole(CommunityRole communityRole, Long communityId);
}

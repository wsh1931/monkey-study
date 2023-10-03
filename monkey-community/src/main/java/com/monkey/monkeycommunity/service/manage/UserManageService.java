package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.vo.UserManageVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserManageService {
    // 查询用户信息集合
    R queryUserInfoList(Long communityId, Long currentPage, Integer pageSize);

    // 查询社区角色列表
    R queryRoleList(Long communityId);

    // 更新用户角色
    R updateUserRole(Long userId, Long roleId, Long communityId);

    // 删除用户角色
    R deleteUserRole(Long userId, Long communityId);

    // 模糊查询用户列表
    R queryUserListByVague(String userId, String roleId, Long communityId, Long currentPage, Integer pageSize);

    // 导出用户数据
    void exportData(List<UserManageVo> userManageVoList, HttpServletResponse response) throws Exception;

    // 邀请用户
    R inviteUser(Long communityId, Long inviteUserId, Long inviteRoleId, Long nowUserId);
}

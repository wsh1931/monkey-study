package com.monkey.monkeycommunity.controller.manage;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.vo.UserManageVo;
import com.monkey.monkeycommunity.service.manage.UserManageService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/30 11:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区用户管理")
@RestController
@RequestMapping("/monkey-community/manage/userManage")
public class UserManageController {
    @Resource
    private UserManageService userManageService;

    @ApiOperation("查询用户信息集合")
    @GetMapping("/queryUserInfoList")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R queryUserInfoList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                               @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userManageService.queryUserInfoList(communityId, currentPage, pageSize);
    }

    @ApiOperation("查询社区角色列表")
    @GetMapping("/queryRoleList")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R queryRoleList(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userManageService.queryRoleList(communityId);
    }

    @ApiOperation("更新用户角色")
    @PutMapping("/updateUserRole")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R updateUserRole(@RequestParam("roleId") @ApiParam("角色id") Long roleId,
                            @RequestParam("userId") @ApiParam("用户id") Long userId,
                            @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userManageService.updateUserRole(userId, roleId, communityId);
    }

    @ApiOperation("删除用户角色")
    @DeleteMapping("/deleteUserRole")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R deleteUserRole(@RequestParam("userId") @ApiParam("用户id") Long userId,
                            @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userManageService.deleteUserRole(userId, communityId);
    }

    @ApiOperation("模糊查询用户列表")
    @GetMapping("/queryUserListByVague")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R queryUserListByVague(@RequestParam("roleId") @ApiParam("角色名") String roleId,
                                  @RequestParam("userId") @ApiParam("用户名") String userId,
                                  @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userManageService.queryUserListByVague(userId, roleId, communityId, currentPage, pageSize);
    }

    @ApiOperation("导出用户数据")
    @GetMapping("/exportData")
    public void exportData(@RequestParam("userList") @ApiParam("用户集合字符串")String userListStr,
                           HttpServletResponse response) throws Exception{
        List<UserManageVo> userManageVoList = JSONObject.parseArray(userListStr, UserManageVo.class);
        userManageService.exportData(userManageVoList, response);
    }

    @ApiOperation("邀请用户")
    @PostMapping("/inviteUser")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).USER_MANAGE.perms + #communityId)")
    public R inviteUser(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                        @RequestParam("inviteRoleId") @ApiParam("角色名") Long inviteRoleId,
                        @RequestParam("inviteUserId") @ApiParam("用户名") Long inviteUserId) {
        long nowUserId = Long.parseLong(JwtUtil.getUserId());
        return userManageService.inviteUser(communityId, inviteUserId, inviteRoleId, nowUserId);
    }
}


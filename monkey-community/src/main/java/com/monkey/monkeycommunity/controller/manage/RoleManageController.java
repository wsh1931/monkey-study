package com.monkey.monkeycommunity.controller.manage;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import com.monkey.monkeycommunity.service.manage.RoleManageService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/2 11:36
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区角色管理")
@RestController
@RequestMapping("/monkey-community/manage/roleManage")
public class RoleManageController {
    @Resource
    private RoleManageService roleManageService;

    @ApiOperation("查询角色管理列表")
    @GetMapping("/queryRoleManageList")
    public R queryRoleManageList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                 @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return roleManageService.queryRoleManageList(communityId, currentPage, pageSize);
    }

    @ApiOperation("保存下设头衔")
    @PostMapping("/preserveDownName")
    public R preserveDownName(@RequestParam("downName") @ApiParam("下设头衔名称") String downName,
                              @RequestParam("roleId") @ApiParam("角色id") Long roleId) {
        return roleManageService.preserveDownName(roleId, downName);
    }

    @ApiOperation("提交编辑角色信息")
    @PutMapping("/submitEditRole")
    public R submitEditRole(@RequestParam("communityRoleStr") @ApiParam("社区角色字符串")String communityRoleStr) {
        CommunityRole communityRole = JSONObject.parseObject(communityRoleStr, CommunityRole.class);
        return roleManageService.submitEditRole(communityRole);
    }

    @ApiOperation("删除下设头衔")
    @DeleteMapping("/deleteDownName")
    public R deleteDownName(@RequestParam("downName") @ApiParam("下拉头衔") String downName,
                            @RequestParam("roleId") @ApiParam("角色id") Long roleId) {
        return roleManageService.deleteDownName(downName, roleId);
    }

    @ApiOperation("删除用户角色")
    @GetMapping("/deleteRole")
    public R deleteRole(@RequestParam("roleId") @ApiParam("角色id") Long roleId,
                        @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return roleManageService.deleteRole(roleId, communityId);
    }

    @ApiOperation("新增社区角色")
    @PostMapping("/addRole")
    public R addRole(@RequestParam("communityRoleStr") @ApiParam("社区角色字符串") String communityRoleStr,
                     @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        CommunityRole communityRole = JSONObject.parseObject(communityRoleStr, CommunityRole.class);
        return roleManageService.addRole(communityRole, communityId);
    }
}

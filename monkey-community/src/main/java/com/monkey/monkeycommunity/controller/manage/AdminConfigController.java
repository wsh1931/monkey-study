package com.monkey.monkeycommunity.controller.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.manage.AdminConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/6 16:59
 * @version: 1.0
 * @description:
 */
@Api(tags = "管理员配置")
@RestController
@RequestMapping("/monkey-community/manage/adminConfig")
public class AdminConfigController {
    @Resource
    private AdminConfigService adminConfigService;

    @ApiOperation("查询社区管理列表")
    @GetMapping("/queryCommunityManager")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).ADMINISTRATOR_CONFIG.perms + #communityId)")
    public R queryCommunityManager(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                   @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                   @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                   @RequestParam("manageIdx") @ApiParam("管理员编号")String manageIdx) {
        return adminConfigService.queryCommunityManager(communityId, currentPage, pageSize, manageIdx);
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("/deleteManager")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).ADMINISTRATOR_CONFIG.perms + #communityId)")
    public R deleteManager(@RequestParam("communityManageId") @ApiParam("社区管理id") Long communityManageId,
                           @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return adminConfigService.deleteManager(communityManageId);
    }

    @ApiOperation("新增管理员")
    @PostMapping("/addManager")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).ADMINISTRATOR_CONFIG.perms + #communityId)")
    public R addManager(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                        @RequestParam("userId") @ApiParam("用户id") String userId) {
        return adminConfigService.addManager(communityId, userId);
    }
}

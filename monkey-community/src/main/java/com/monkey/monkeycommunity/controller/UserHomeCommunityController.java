package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.UserHomeCommunityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/25 14:47
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页调用社区功能接口")
@RestController
@RequestMapping("/monkey-community/user/home/community")
public class UserHomeCommunityController {
    @Resource
    private UserHomeCommunityService userHomeCommunityService;

    @ApiOperation("通过用户id查询社区集合")
    @GetMapping("/queryCommunityByUserId")
    public R queryCommunityByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                    @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeCommunityService.queryCommunityByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("删除社区")
    @DeleteMapping("/deleteCommunity")
    @PreAuthorize("@commonAuthority.isSameUser(#userId)")
    public R deleteResource(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                            @RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userHomeCommunityService.deleteCommunity(communityId);
    }
}

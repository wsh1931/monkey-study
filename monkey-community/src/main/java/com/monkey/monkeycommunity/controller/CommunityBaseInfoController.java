package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityBaseInfoService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/12 16:38
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程基本信息")
@RestController
@RequestMapping("/monkey-community/community/baseInfo")
public class CommunityBaseInfoController {
    @Resource
    private CommunityBaseInfoService communityBaseInfoService;

    @ApiOperation("查询社区基本信息")
    @GetMapping("/queryCommunityBaseInfo/ByCommunityId")
    public R queryCommunityBaseInfoByCommunityId(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityBaseInfoService.queryCommunityBaseInfoByCommunityId(communityId);
    }

    @ApiOperation("得到社区标签列表")
    @GetMapping("/queryCommunityLabelList")
    public R queryCommunityLabelList(@RequestParam("communityId") @ApiParam("社区id")Long communityId) {
        return communityBaseInfoService.queryCommunityLabelList(communityId);
    }

    @ApiOperation("得到社区管理员列表")
    @GetMapping("/queryCommunityManagerList")
    public R queryCommunityManagerList(@RequestParam("communityId") @ApiParam("社区id")Long communityId) {
        return communityBaseInfoService.queryCommunityManagerList(communityId);
    }

    @ApiOperation("判断当前登录用户是否是社区管理员")
    @GetMapping("/judgeUserIsCommunityManager/AndIsInCommunity")
    public R judgeUserIsCommunityManagerAndIsInCommunity(@RequestParam("communityId")Long communityId) {
        String userId = JwtUtil.getUserId();
        return communityBaseInfoService.judgeUserIsCommunityManagerAndIsInCommunity(communityId, userId);
    }
}

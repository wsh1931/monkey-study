package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.PublishArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/6 16:33
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-community/publish")
@Api(tags = "发布社区文章")
public class PublishArticleController {
    @Resource
    private PublishArticleService publishArticleService;

    @ApiOperation("查询社区角色列表")
    @GetMapping("/queryCommunityRoleList")
    public R queryCommunityRoleList(@RequestParam("communityId")Long communityId) {
        return publishArticleService.queryCommunityRoleList(communityId);
    }

    @ApiOperation("通过社区id查询社区频道列表")
    @GetMapping("/queryCommunityChannelListByCommunityId")
    public R queryCommunityChannelListByCommunityId(@RequestParam("communityId")Long communityId) {
        return publishArticleService.queryCommunityChannelListByCommunityId(communityId);
    }
}

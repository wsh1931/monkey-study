package com.monkey.monkeysearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;
import com.monkey.monkeysearch.service.CommunityArticleFeignService;
import com.monkey.monkeysearch.service.CommunityFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/12 14:23
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区功能调用搜索模块接口")
@RestController
@RequestMapping("/monkey-search/community/feign")
public class CommunityFeignController {
    @Resource
    private CommunityFeignService communityFeignService;

    @ApiOperation("社区成员数 + 1")
    @PutMapping("/communityMemberAddOne")
    public R communityMemberAddOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityFeignService.communityMemberAddOne(communityId);
    }

    @ApiOperation("社区成员数 - 1")
    @PutMapping("/communityMemberSubOne")
    public R communityMemberSubOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityFeignService.communityMemberSubOne(communityId);
    }

    @ApiOperation("社区文章数 + 1")
    @PutMapping("/communityArticleAddOne")
    public R communityArticleAddOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityFeignService.communityArticleAddOne(communityId);
    }

    @ApiOperation("社区文章数 - 1")
    @PutMapping("/communityArticleSubOne")
    public R communityArticleSubOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityFeignService.communityArticleSubOne(communityId);
    }

    @ApiOperation("创建社区")
    @PutMapping("/createCommunity")
    public R createCommunity(@RequestParam("communityStr") @ApiParam("社区索引类") String communityStr) {
        ESCommunityIndex esCommunityIndex = JSONObject.parseObject(communityStr, ESCommunityIndex.class);
        return communityFeignService.createCommunity(esCommunityIndex);
    }

    @ApiOperation("删除社区")
    @DeleteMapping("/deleteCommunity")
    public R deleteCommunity(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityFeignService.deleteCommunity(communityId);
    }
}

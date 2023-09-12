package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;
import com.monkey.monkeycommunity.service.PublishArticleService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("发布社区文章")
    @PostMapping("/publishArticle")
    public R publishArticle(@RequestParam("communityArticle") String communityArticleStr,
                            @RequestParam("communityId")Long communityId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        CommunityArticleVo communityArticleVo = JSONObject.parseObject(communityArticleStr, CommunityArticleVo.class);
        return publishArticleService.publishArticle(userId, communityId, communityArticleVo);
    }

    @ApiOperation("通过社区id查询除了全部的社区频道列表")
    @GetMapping("/queryCommunityChannelListByCommunityIdExceptAll")
    public R queryCommunityChannelListByCommunityIdExceptAll(@RequestParam("communityId")Long communityId) {
        return publishArticleService.queryCommunityChannelListByCommunityIdExceptAll(communityId);
    }
}

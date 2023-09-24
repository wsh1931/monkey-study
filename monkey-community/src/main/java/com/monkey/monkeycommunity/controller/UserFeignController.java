package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/24 14:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户调用社区模块feign")
@RestController
@RequestMapping("/monkey-community/user/feign")
public class UserFeignController {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("社区文章收藏数 + 1")
    @PostMapping("/community/article/collect/add/one/{communityArticleId}")
    public void communityArticleCollectAddOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId) {
        userFeignService.communityArticleCollectAddOne(communityArticleId);
    }

    @ApiOperation("社区文章收藏数 - 1")
    @PostMapping("/community/article/collect/sub/one/{communityArticleId}")
    public void communityArticleCollectSubOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId) {
        userFeignService.communityArticleCollectSubOne(communityArticleId);
    }
}

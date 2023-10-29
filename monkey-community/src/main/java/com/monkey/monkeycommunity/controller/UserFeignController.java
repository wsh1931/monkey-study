package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("通过社区文章id得到社区文章信息")
    @GetMapping("/queryCommunityArticleById/{communityArticleId}")
    public R queryCommunityArticleById(@PathVariable @ApiParam("社区文章id") Long communityArticleId) {
        return userFeignService.queryCommunityArticleById(communityArticleId);
    }

    @ApiOperation("通过社区文章id和评论id得到社区文章信息")
    @GetMapping("/queryCommunityArticleAndCommentById")
    public R queryCommunityArticleAndCommentById(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                       @RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return userFeignService.queryCommunityArticleAndCommentById(communityArticleId, commentId);
    }

    @ApiOperation("通过社区文章id得到社区文章作者id")
    @GetMapping("/queryCommunityArticleAuthorById")
    public Long queryCommunityArticleAuthorById(@RequestParam("associationId") @ApiParam("文章id") Long communityArticleId) {
        return userFeignService.queryCommunityArticleAuthorById(communityArticleId);
    }
}

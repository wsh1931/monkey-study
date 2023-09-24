package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityDetailCardService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/11 10:16
 * @version: 1.0
 * @description: 课程详情card类
 */
@Api(tags = "课程卡片接口")
@RestController
@RequestMapping("/monkey-community/community/detail/card")
public class CommunityDetailCardController {
    @Resource
    private CommunityDetailCardService communityDetailCardService;

    @ApiOperation("判断是否有显示隐藏框的权力")
    @GetMapping("/judgePower")
    public R judgePower(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        String userId = JwtUtil.getUserId();
        return communityDetailCardService.judgePower(communityId, userId);
    }

    @ApiOperation("删除社区文章")
    @DeleteMapping("/deleteArticle")
    public R deleteArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                           @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityDetailCardService.deleteArticle(articleId, communityId);
    }

    @ApiOperation("将文章设置为精选内容")
    @PutMapping("/setExcellentArticle")
    public R setExcellentArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return communityDetailCardService.setExcellentArticle(articleId);
    }

    @ApiOperation("取消文章精选内容")
    @PutMapping("/cancelExcellentArticle")
    public R cancelExcellentArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return communityDetailCardService.cancelExcellentArticle(articleId);
    }

    @ApiOperation("取消文章置顶")
    @PutMapping("/cancelTopArticle")
    public R cancelTopArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return communityDetailCardService.cancelTopArticle(articleId);
    }

    @ApiOperation("将文章设置为置顶内容")
    @PutMapping("/setTopArticle")
    public R setTopArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return communityDetailCardService.setTopArticle(articleId);
    }
}

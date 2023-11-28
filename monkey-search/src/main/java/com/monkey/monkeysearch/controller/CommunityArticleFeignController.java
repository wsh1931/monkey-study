package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.CommunityArticleFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 17:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区文章功能调用搜索模块接口")
@RestController
@RequestMapping("/monkey-search/community/article/feign")
public class CommunityArticleFeignController {
    @Resource
    private CommunityArticleFeignService communityArticleFeignService;
    @ApiOperation("社区文章游览数 + 1")
    @PutMapping("/communityArticleViewAddOne")
    public R communityArticleViewAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleViewAddOne(communityArticleId);
    }

    @ApiOperation("社区文章评论数 + 1")
    @PutMapping("/communityArticleCommentCountAdd")
    public R communityArticleCommentCountAdd(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleCommentCountAdd(communityArticleId);
    }

    @ApiOperation("社区文章评论数减去对应值")
    @PutMapping("/communityArticleCommentSub")
    public R communityArticleCommentSub(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                              @RequestParam("sum") @ApiParam("减去的数目") Long sum) {
        return communityArticleFeignService.communityArticleCommentSub(communityArticleId, sum);
    }

    @ApiOperation("社区文章收藏数 + 1")
    @PutMapping("/communityArticleCollectCountAddOne")
    public R communityArticleCollectCountAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleCollectCountAddOne(communityArticleId);
    }
    @ApiOperation("社区文章收藏数 - 1")
    @PutMapping("/communityArticleCollectCountSubOne")
    public R communityArticleCollectCountSubOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleCollectCountSubOne(communityArticleId);
    }

    @ApiOperation("社区文章点赞人数 + 1")
    @PutMapping("/communityArticleLikeCountAddOne")
    public R communityArticleLikeCountAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleLikeCountAddOne(communityArticleId);
    }

    @ApiOperation("社区文章点赞人数 - 1")
    @PutMapping("/communityArticleLikeCountSubOne")
    public R communityArticleLikeCountSubOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleFeignService.communityArticleLikeCountSubOne(communityArticleId);
    }


    @ApiOperation("更新社区文章评分")
    @PutMapping("/updateCommunityArticleScore")
    public R updateCommunityArticleScore(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                               @RequestParam("score") @ApiParam("社区文章评分") Float score) {
        return communityArticleFeignService.updateCommunityArticleScore(communityArticleId, score);
    }

    @ApiOperation("删除社区文章")
    @DeleteMapping("/deleteCommunityArticle")
    R deleteCommunityArticle(@RequestParam("删除社区文章") @ApiParam("社区文章id") String communityArticleId) {
        return communityArticleFeignService.deleteCommunityArticle(communityArticleId);
    }

}

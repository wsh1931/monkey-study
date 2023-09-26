package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityRankService;
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
 * @date: 2023/9/25 10:22
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区排行接口")
@RestController
@RequestMapping("/monkey-community/rank")
public class CommunityRankController {
    @Resource
    private CommunityRankService communityRankService;

    @ApiOperation("查询社区游览数排行")
    @GetMapping("/queryCommunityViewCount")
    public R queryCommunityViewCount() {
        return communityRankService.queryCommunityViewCount();
    }

    @ApiOperation("查询社区成员数排行")
    @GetMapping("/queryCommunityMemberCount")
    public R queryCommunityMemberCount() {
        return communityRankService.queryCommunityMemberCount();
    }

    @ApiOperation("查询社区文章数排行")
    @GetMapping("/queryCommunityArticleCount")
    public R queryCommunityArticleCount() {
        return communityRankService.queryCommunityArticleCount();
    }

    @ApiOperation("查询社区点赞数排行")
    @GetMapping("/queryCommunityLikeCount")
    public R queryCommunityLikeCount() {
        return communityRankService.queryCommunityLikeCount();
    }

    @ApiOperation("查询社区评论数排行")
    @GetMapping("/queryCommunityCommentCount")
    public R queryCommunityCommentCount() {
        return communityRankService.queryCommunityCommentCount();
    }

    @ApiOperation("查询社区评分数排行")
    @GetMapping("/queryCommunityScoreCount")
    public R queryCommunityScoreCount() {
        return communityRankService.queryCommunityScoreCount();
    }

    @ApiOperation("查询社区收藏数排行")
    @GetMapping("/queryCommunityCollectCount")
    public R queryCommunityCollectCount() {
        return communityRankService.queryCommunityCollectCount();
    }
}

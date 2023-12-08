package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityDetailCardService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @ApiOperation("将文章设置为精选内容")
    @PutMapping("/setExcellentArticle")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_MANAGE.perm + #communityId, " +
            "T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_PRIME_MANAGE.perm + #communityId)")
    public R setExcellentArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                                 @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityDetailCardService.setExcellentArticle(articleId);
    }

    @ApiOperation("取消文章精选内容")
    @PutMapping("/cancelExcellentArticle")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_MANAGE.perm + #communityId, " +
            "T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_PRIME_MANAGE.perm + #communityId)")
    public R cancelExcellentArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                                    @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityDetailCardService.cancelExcellentArticle(articleId);
    }

    @ApiOperation("取消文章置顶")
    @PutMapping("/cancelTopArticle")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_MANAGE.perm + #communityId, " +
            "T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_PRIME_MANAGE.perm + #communityId)")
    public R cancelTopArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                              @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityDetailCardService.cancelTopArticle(articleId);
    }

    @ApiOperation("将文章设置为置顶内容")
    @PutMapping("/setTopArticle")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_MANAGE.perm + #communityId, " +
            "T(com.monkey.monkeyUtils.constants.CommunityAuthorityEnum).COMMUNITY_PRIME_MANAGE.perm + #communityId)")
    public R setTopArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                           @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return communityDetailCardService.setTopArticle(articleId);
    }
}

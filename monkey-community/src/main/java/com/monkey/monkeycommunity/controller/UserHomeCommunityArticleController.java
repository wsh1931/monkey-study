package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.service.UserHomeCommunityArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/27 9:34
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页社区文章接口")
@RestController
@RequestMapping("/monkey-community/user/home/community/article")
public class UserHomeCommunityArticleController {
    @Resource
    private UserHomeCommunityArticleService userHomeCommunityArticleService;

    @ApiOperation("通过用户id查询社区文章集合")
    @GetMapping("/queryCommunityArticleByUserId")
    public R queryCommunityArticleByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                           @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeCommunityArticleService.queryCommunityArticleByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("删除社区文章")
    @DeleteMapping("/deleteCommunityArticle")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R deleteCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                    @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userHomeCommunityArticleService.deleteCommunityArticle(communityArticleId, communityId);
    }
}

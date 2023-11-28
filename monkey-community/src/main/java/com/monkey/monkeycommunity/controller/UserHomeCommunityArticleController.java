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

    @ApiOperation("查询社区文章信息")
    @GetMapping("/queryCommunityArticle")
    public R queryCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return userHomeCommunityArticleService.queryCommunityArticle(communityArticleId);
    }

    @ApiOperation("删除数据库中的图片")
    @DeleteMapping("/deleteCommunityArticlePicture")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R deleteCommunityArticlePicture(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return userHomeCommunityArticleService.deleteCommunityArticlePicture(communityArticleId);
    }

    @ApiOperation("更新社区文章")
    @PutMapping("/updateCommunityArticle")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R updateCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                    @RequestParam("communityArticleStr") @ApiParam("社区文章实体类字符串") String communityArticleStr) {
        CommunityArticle communityArticle = JSONObject.parseObject(communityArticleStr, CommunityArticle.class);
        return userHomeCommunityArticleService.updateCommunityArticle(communityArticle);
    }

    @ApiOperation("删除社区文章")
    @DeleteMapping("/deleteCommunityArticle")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R deleteCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return userHomeCommunityArticleService.deleteCommunityArticle(communityArticleId);
    }
}

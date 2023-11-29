package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.service.CommunityArticleEditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 17:23
 * @version: 1.0
 * @description:
 */
@Api(tags = "编辑社区文章接口")
@RestController
@RequestMapping("/community/article/edit")
public class CommunityArticleEditContoller {
    @Resource
    private CommunityArticleEditService communityArticleEditService;

    @ApiOperation("查询社区文章信息")
    @GetMapping("/queryCommunityArticle")
    public R queryCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleEditService.queryCommunityArticle(communityArticleId);
    }

    @ApiOperation("删除数据库中的图片")
    @DeleteMapping("/deleteCommunityArticlePicture")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R deleteCommunityArticlePicture(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return communityArticleEditService.deleteCommunityArticlePicture(communityArticleId);
    }

    @ApiOperation("更新社区文章")
    @PutMapping("/updateCommunityArticle")
    @PreAuthorize("@communityCustomAuthority.judgeIsCommunityArticleAuthor(#communityArticleId)")
    public R updateCommunityArticle(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                    @RequestParam("communityArticleStr") @ApiParam("社区文章实体类字符串") String communityArticleStr) {
        CommunityArticle communityArticle = JSONObject.parseObject(communityArticleStr, CommunityArticle.class);
        return communityArticleEditService.updateCommunityArticle(communityArticle);
    }
}

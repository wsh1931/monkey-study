package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;
import com.monkey.monkeycommunity.service.CommunityService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/1 17:35
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区文章主页接口")
@RestController
@RequestMapping("/monkey-community/community")
public class CommunityController {
    @Resource
    private CommunityService communityService;

    @ApiOperation("得到一级标签")
    @GetMapping("/getOneLevelLabelList")
    public R getOneLevelLabelList() {
        return communityService.getOneLevelLabelList();
    }

    @ApiOperation("通过一级标签id得到二级标签列表")
    @GetMapping("/getTwoLabelListByOneLabelId")
    public R getTwoLabelListByOneLabelId(@RequestParam("labelOneId") @ApiParam("一级标签id") Long labelOneId) {
        return communityService.getTwoLabelListByOneLabelId(labelOneId);
    }

    @ApiOperation("得到与我有关社区文章列表")
    @GetMapping("/queryWithMeArticleList")
    public R queryWithMeArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.queryWithMeArticleList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到点赞最多文章列表")
    @GetMapping("/queryLikeArticleList")
    public R queryLikeArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryLikeArticleList(currentPage, pageSize);
    }

    @ApiOperation("得到回复最多文章列表")
    @GetMapping("/queryReplyArticleList")
    public R queryReplyArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                   @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryReplyArticleList(currentPage, pageSize);
    }

    @ApiOperation("得到游览最多文章列表")
    @GetMapping("/queryViewArticleList")
    public R queryViewArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryViewArticleList(currentPage, pageSize);
    }

    @ApiOperation("得到收藏最高文章列表")
    @GetMapping("/queryCollectArticleList")
    public R queryCollectArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryCollectArticleList(currentPage, pageSize);
    }

    @ApiOperation("得到最热文章列表")
    @GetMapping("/queryHireArticleList")
    public R queryHireArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryHireArticleList(currentPage, pageSize);
    }

    @ApiOperation("得到最新文章列表")
    @GetMapping("/queryLatestArticleList")
    public R queryLatestArticleList(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return communityService.queryLatestArticleList(currentPage, pageSize);
    }

    @ApiOperation("通过搜索字段得到一级标签")
    @GetMapping("/queryOneLabel")
    public R queryOneLabel(@RequestParam("search") @ApiParam("模糊搜索字段") String search) {
        return communityService.queryOneLabel(search);
    }

    @ApiOperation("通过条件搜索文章")
    @GetMapping("/searchArticle")
    public R searchArticle(@RequestParam("communityName") @ApiParam("社区名") String communityName,
                           @RequestParam("tabList") @ApiParam("标签集合字符串") String tabListStr,
                           @RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                           @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        List<CommunityClassificationLabel> communityClassificationLabelList = JSONArray.parseArray(tabListStr, CommunityClassificationLabel.class);
        return communityService.searchArticle(communityName, communityClassificationLabelList, currentPage, pageSize);
    }

    @ApiOperation("查询热门社区列表")
    @GetMapping("/queryHireCommunityList")
    public R queryHireCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityService.queryHireCommunityList(userId);
    }

    @ApiOperation("查询最新社区列表")
    @GetMapping("/queryLatestCommunityList")
    public R queryLatestCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityService.queryLatestCommunityList(userId);
    }

    @ApiOperation("查询我的社区列表")
    @GetMapping("/queryWithMeCommunityList")
    public R queryWithMeCommunityList() {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.queryWithMeCommunityList(userId);
    }

    @ApiOperation("加入社区实现")
    @PostMapping("/applicationAddCommunity")
    public R applicationAddCommunity(@RequestParam("community") @ApiParam("社区字符串") String communityStr) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        Community community = JSONObject.parseObject(communityStr, Community.class);
        return communityService.applicationAddCommunity(userId, community);
    }

    @ApiOperation("退出社区实现")
    @DeleteMapping("/turnOutCommunity")
    public R turnOutCommunity(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.turnOutCommunity(userId, communityId);
    }

    @ApiOperation("社区文章游览数 + 1")
    @PutMapping("/articleViewCount/addOne")
    public R communityArticleViewCountAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id")Long communityArticleId) {
        return communityService.communityArticleViewCountAddOne(communityArticleId);
    }

    @ApiOperation("查询用户管理社区集合")
    @GetMapping("/queryUserManageCommunity")
    public R queryUserManageCommunity(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        return communityService.queryUserManageCommunity(userId, currentPage, pageSize);
    }
}

package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;
import com.monkey.monkeycommunity.service.CommunityService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/1 17:35
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-community/community")
public class CommunityController {
    @Resource
    private CommunityService communityService;

    // 得到一级标签
    @GetMapping("/getOneLevelLabelList")
    public R getOneLevelLabelList() {
        return communityService.getOneLevelLabelList();
    }

    // 通过一级标签id得到二级标签列表
    @GetMapping("/getTwoLabelListByOneLabelId")
    public R getTwoLabelListByOneLabelId(@RequestParam Map<String, String> data) {
        long labelOneId = Long.parseLong(data.get("labelOneId"));
        return communityService.getTwoLabelListByOneLabelId(labelOneId);
    }

    // 得到与我有关社区文章列表
    @GetMapping("/queryWithMeArticleList")
    public R queryWithMeArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.queryWithMeArticleList(userId, currentPage, pageSize);
    }

    // 得到点赞最多文章列表
    @GetMapping("/queryLikeArticleList")
    public R queryLikeArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryLikeArticleList(currentPage, pageSize);
    }

    // 得到回复最多文章列表
    @GetMapping("/queryReplyArticleList")
    public R queryReplyArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryReplyArticleList(currentPage, pageSize);
    }

    // 得到游览最多文章列表
    @GetMapping("/queryViewArticleList")
    public R queryViewArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryViewArticleList(currentPage, pageSize);
    }

    // 得到收藏最高文章列表
    @GetMapping("/queryCollectArticleList")
    public R queryCollectArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryCollectArticleList(currentPage, pageSize);
    }

    // 得到最热文章列表
    @GetMapping("/queryHireArticleList")
    public R queryHireArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryHireArticleList(currentPage, pageSize);
    }

    // 得到最新文章列表
    @GetMapping("/queryLatestArticleList")
    public R queryLatestArticleList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return communityService.queryLatestArticleList(currentPage, pageSize);
    }

    // 通过搜索字段得到一级标签
    @GetMapping("/queryOneLabel")
    public R queryOneLabel(@RequestParam("search") String search) {
        return communityService.queryOneLabel(search);
    }

    // 通过条件搜索文章
    @GetMapping("/searchArticle")
    public R searchArticle(@RequestParam("communityName") String communityName,
                           @RequestParam("tabList") String tabListStr,
                           @RequestParam("currentPage") Integer currentPage,
                           @RequestParam("pageSize")Integer pageSize) {
        List<CommunityClassificationLabel> communityClassificationLabelList = JSONArray.parseArray(tabListStr, CommunityClassificationLabel.class);
        return communityService.searchArticle(communityName, communityClassificationLabelList, currentPage, pageSize);
    }

    // 查询热门社区列表
    @GetMapping("/queryHireCommunityList")
    public R queryHireCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityService.queryHireCommunityList(userId);
    }

    // 查询最新社区列表
    @GetMapping("/queryLatestCommunityList")
    public R queryLatestCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityService.queryLatestCommunityList(userId);
    }

    // 查询我的社区列表
    @GetMapping("/queryWithMeCommunityList")
    public R queryWithMeCommunityList() {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.queryWithMeCommunityList(userId);
    }

    // 加入社区实现
    @PostMapping("/applicationAddCommunity")
    public R applicationAddCommunity(@RequestParam("community") String communityStr) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        Community community = JSONObject.parseObject(communityStr, Community.class);
        return communityService.applicationAddCommunity(userId, community);
    }

    // 退出社区实现
    @DeleteMapping("/turnOutCommunity")
    public R turnOutCommunity(@RequestParam("communityId")Long communityId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityService.turnOutCommunity(userId, communityId);
    }
}

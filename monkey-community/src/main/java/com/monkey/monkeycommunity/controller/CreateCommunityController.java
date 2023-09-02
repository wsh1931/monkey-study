package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.service.CreateCommunityService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/2 15:08
 * @version: 1.0
 * @description: 创建社区
 */
@RestController
@RequestMapping("/monkey-community/create")
public class CreateCommunityController {
    @Resource
    private CreateCommunityService communityService;

    // 得到社区属性列表
    @GetMapping("/queryCommunityAttributeList")
    public R queryCommunityAttributeList() {
        return communityService.queryCommunityAttributeList();
    }

    // 得到社区分类列表
    @GetMapping("/queryCommunityClassificationList")
    public R queryCommunityClassificationList() {
        return communityService.queryCommunityClassificationList();
    }

    // 创建社区
    @PostMapping("/createCommunity")
    public R createCommunity(@RequestParam("community") String communityStr) {
        Community community = JSONObject.parseObject(communityStr, Community.class);
        return communityService.createCommunity(community);
    }
}

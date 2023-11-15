package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.SearchFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 16:30
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜索模块调用社区模块接口")
@RestController
@RequestMapping("/monkey-community/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("得到所有用户所有社区文章，点赞，收藏，游览数")
    @GetMapping("/queryAllUserCommunityArticleInfo")
    public R queryAllUserCommunityArticleInfo() {
        return searchFeignService.queryAllUserCommunityArticleInfo();
    }
    @ApiOperation("查询所有社区文章数据")
    @GetMapping("/queryAllCommunityArticle")
    public R queryAllCommunityArticle() {
        return searchFeignService.queryAllCommunityArticle();
    }

    @ApiOperation("查询所有社区数据")
    @GetMapping("/queryAllCommunity")
    public R queryAllCommunity() {
        return searchFeignService.queryAllCommunity();
    }
}

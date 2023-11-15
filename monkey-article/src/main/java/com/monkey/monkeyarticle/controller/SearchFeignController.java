package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.service.SearchFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/8 8:53
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜索模块feign接口")
@RestController
@RequestMapping("/monkey-article/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("得到所有用户所有文章，点赞，收藏，游览数")
    @GetMapping("/queryAllUserArticleInfo")
    public R queryAllUserArticleInfo() {
        return searchFeignService.queryAllUserArticleInfo();
    }
    @ApiOperation("查询所有文章")
    @GetMapping("/queryAllArticle")
    public R queryAllArticle() {
        return searchFeignService.queryAllArticle();
    }
}

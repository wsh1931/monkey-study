package com.monkey.monkeysearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.service.ArticleFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/10 17:32
 * @version: 1.0
 * @description:
 */
@Api(tags = "文章调用搜索模块接口")
@RestController
@RequestMapping("/monkey-search/article/feign")
public class ArticleFeignController {
    @Resource
    private ArticleFeignService articleFeignService;

    @ApiOperation("删除elasticsearch文章")
    @DeleteMapping("/deleteArticle")
    R deleteArticle(@RequestParam("articleId") @ApiParam("文章id") String articleId) {
        return articleFeignService.deleteArticle(articleId);
    }

    @ApiOperation("更新文章实体类")
    @PutMapping("/updateArticle")
    R updateArticle(@RequestParam @ApiParam("文章实体类") String articleStr) {
        ESArticleIndex esArticleIndex = JSONObject.parseObject(articleStr, ESArticleIndex.class);
        return articleFeignService.updateArticle(esArticleIndex);
    }

    @ApiOperation("文章游览数 + 1")
    @PutMapping("/articleViewAddOne")
    public R articleViewAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleViewAddOne(articleId);
    }

    @ApiOperation("文章评论数 + 1")
    @PutMapping("/articleCommentCountAdd")
    public R articleReplyCountAdd(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleCommentCountAdd(articleId);
    }

    @ApiOperation("文章点赞数 + 1")
    @PutMapping("/articleLikeCountAddOne")
    public R articleLikeCountAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleLikeCountAddOne(articleId);
    }

    @ApiOperation("文章点赞数 - 1")
    @PutMapping("/articleLikeCountSubOne")
    public R articleLikeCountSubOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleLikeCountSubOne(articleId);
    }
    @ApiOperation("文章收藏数 + 1")
    @PutMapping("/articleCollectCountAddOne")
    public R articleCollectCountAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleCollectCountAddOne(articleId);
    }
    @ApiOperation("文章收藏数 - 1")
    @PutMapping("/articleCollectCountSubOne")
    public R articleCollectCountSubOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleFeignService.articleCollectCountSubOne(articleId);
    }
}

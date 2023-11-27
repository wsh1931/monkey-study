package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESCommunityArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 16:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch社区文章接口")
@RestController
@RequestMapping("/monkey-search/community/article")
public class ESCommunityArticleController {
    @Resource
    private ESCommunityArticleService esCommunityArticleService;

    @ApiOperation("查询所有社区文章文档")
    @GetMapping("/queryAllCommunityArticleDocument")
    public R queryAllCommunityArticleDocument() {
        return esCommunityArticleService.queryAllCommunityArticleDocument();
    }

    @ApiOperation("删除所有社区文章文档")
    @DeleteMapping("/deleteAllCommunityArticleDocument")
    public R deleteAllCommunityArticleDocument() {
        return esCommunityArticleService.deleteAllCommunityArticleDocument();
    }
    @ApiOperation("将文章数据库中所有数据存入elasticsearch文章文档中")
    @PostMapping("/insertCommunityArticleDocument")
    public R insertCommunityArticleDocument() {
        return esCommunityArticleService.insertCommunityArticleDocument();
    }

    @ApiOperation("查询综合社区文章列表")
    @GetMapping("/queryComprehensiveCommunityArticle")
    public R queryComprehensiveCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                      @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryComprehensiveCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询评论最多社区文章列表")
    @GetMapping("/queryCommentCommunityArticle")
    public R queryCommentCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryCommentCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多社区文章列表")
    @GetMapping("/queryCollectCommunityArticle")
    public R queryCollectCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryCollectCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询游览数最多社区文章列表")
    @GetMapping("/queryViewCommunityArticle")
    public R queryViewCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                             @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                             @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryViewCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热社区文章列表")
    @GetMapping("/queryHireCommunityArticle")
    public R queryHireCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                             @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                             @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryHireCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新社区文章列表")
    @GetMapping("/queryLatestCommunityArticle")
    public R queryLatestCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryLatestCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询社区文章评分最高的社区文章列表")
    @GetMapping("/queryScoreCommunityArticle")
    public R queryScoreCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryScoreCommunityArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞最多社区文章列表")
    @GetMapping("/queryLikeCommunityArticle")
    public R queryLikeCommunityArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityArticleService.queryLikeCommunityArticle(currentPage, pageSize, keyword);
    }
}

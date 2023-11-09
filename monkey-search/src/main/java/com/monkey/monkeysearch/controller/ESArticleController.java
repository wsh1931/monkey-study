package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/7 16:20
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch文章搜索接口")
@RestController
@RequestMapping("/monkey-search/article")
public class ESArticleController {
    @Resource
    private ESArticleService esArticleService;

    @ApiOperation("将文章数据库中所有数据存入elasticsearch文章文档中")
    @PostMapping("/insertArticleDocument")
    public R insertArticleDocument() {
        return esArticleService.insertArticleDocument();
    }

    @ApiOperation("查询分页文章elasticsearch")
    @GetMapping("/queryArticle")
    public R queryArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                          @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                          @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询综合文章列表")
    @GetMapping("/queryComprehensiveArticle")
    public R queryComprehensiveArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryComprehensiveArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询回复最多文章列表")
    @GetMapping("/queryReplyArticle")
    public R queryReplyArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryReplyArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多文章列表")
    @GetMapping("/queryCollectArticle")
    public R queryCollectArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryCollectArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞数最多文章列表")
    @GetMapping("/queryLikeArticle")
    public R queryLikeArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryLikeArticle(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询游览数最多文章列表")
    @GetMapping("/queryViewArticle")
    public R queryViewArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryViewArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热文章列表")
    @GetMapping("/queryHireArticle")
    public R queryHireArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryHireArticle(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新文章列表")
    @GetMapping("/queryLatestArticle")
    public R queryLatestArticle(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esArticleService.queryLatestArticle(currentPage, pageSize, keyword);
    }

}

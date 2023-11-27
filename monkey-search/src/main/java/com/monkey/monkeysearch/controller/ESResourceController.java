package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/12 16:53
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch资源功能接口")
@RestController
@RequestMapping("/monkey-search/resource")
public class ESResourceController {
    @Resource
    private ESResourceService esResourceService;

    @ApiOperation("查询所有资源文档")
    @GetMapping("/queryAllResourceDocument")
    public R queryAllResourceDocument() {
        return esResourceService.queryAllResourceDocument();
    }

    @ApiOperation("删除所有资源文档")
    @DeleteMapping("/deleteAllResourceDocument")
    public R deleteAllResourceDocument() {
        return esResourceService.deleteAllResourceDocument();
    }

    @ApiOperation("将资源数据库中所有数据存入elasticsearch资源文档中")
    @PostMapping("/insertResourceDocument")
    public R insertResourceDocument() {
        return esResourceService.insertResourceDocument();
    }

    @ApiOperation("查询综合资源列表")
    @GetMapping("/queryComprehensiveResource")
    public R queryComprehensiveResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryComprehensiveResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询评论最多资源列表")
    @GetMapping("/queryCommentResource")
    public R queryCommentResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryCommentResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多资源列表")
    @GetMapping("/queryCollectResource")
    public R queryCollectResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryCollectResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞数最多资源列表")
    @GetMapping("/queryLikeResource")
    public R queryLikeResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryLikeResource(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询游览数最多资源列表")
    @GetMapping("/queryViewResource")
    public R queryViewResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryViewResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询下载数最多资源列表")
    @GetMapping("/queryDownResource")
    public R queryDownResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryDownResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询购买数最多资源列表")
    @GetMapping("/queryBuyResource")
    public R queryBuyResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryBuyResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热资源列表")
    @GetMapping("/queryHireResource")
    public R queryHireResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryHireResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新资源列表")
    @GetMapping("/queryLatestResource")
    public R queryLatestResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryLatestResource(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询资源评分最多的资源列表")
    @GetMapping("/queryScoreResource")
    public R queryScoreResource(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esResourceService.queryScoreResource(currentPage, pageSize, keyword);
    }
}

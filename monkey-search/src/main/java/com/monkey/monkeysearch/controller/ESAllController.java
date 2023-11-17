package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESAllService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/16 9:49
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch全部搜索接口")
@RestController
@RequestMapping("/monkey-search/all")
public class ESAllController {
    @Resource
    private ESAllService esAllService;

    @ApiOperation("查询综合全部列表")
    @GetMapping("/queryComprehensiveAll")
    public R queryComprehensiveAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryComprehensiveAll(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询评论最多全部列表")
    @GetMapping("/queryCommentAll")
    public R queryCommentAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryCommentAll(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多全部列表")
    @GetMapping("/queryCollectAll")
    public R queryCollectAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryCollectAll(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞数最多全部列表")
    @GetMapping("/queryLikeAll")
    public R queryLikeAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryLikeAll(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询游览数最多全部列表")
    @GetMapping("/queryViewAll")
    public R queryViewAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryViewAll(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热全部列表")
    @GetMapping("/queryHireAll")
    public R queryHireAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryHireAll(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新全部列表")
    @GetMapping("/queryLatestAll")
    public R queryLatestAll(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esAllService.queryLatestAll(currentPage, pageSize, keyword);
    }
}

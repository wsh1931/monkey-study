package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESCommunityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/12 10:51
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch社区功能接口")
@RestController
@RequestMapping("/monkey-search/community")
public class ESCommunityController {
    @Resource
    private ESCommunityService esCommunityService;

    @ApiOperation("查询所有社区文档")
    @GetMapping("/queryCommunityDocument")
    public R queryCommunityDocument() {
        return esCommunityService.queryCommunityDocument();
    }

    @ApiOperation("删除所有社区文档")
    @DeleteMapping("/deleteCommunityDocument")
    public R deleteCommunityDocument() {
        return esCommunityService.deleteCommunityDocument();
    }
    @ApiOperation("将社区数据库中所有数据存入elasticsearch社区文档中")
    @PostMapping("/insertCommunityDocument")
    public R insertCommunityDocument() {
        return esCommunityService.insertCommunityDocument();
    }

    @ApiOperation("查询综合社区列表")
    @GetMapping("/queryComprehensiveCommunity")
    public R queryComprehensiveCommunity(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityService.queryComprehensiveCommunity(currentPage, pageSize, keyword);
    }


    @ApiOperation("查询最热社区列表")
    @GetMapping("/queryHireCommunity")
    public R queryHireCommunity(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityService.queryHireCommunity(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新社区列表")
    @GetMapping("/queryLatestCommunity")
    public R queryLatestCommunity(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                         @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                         @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityService.queryLatestCommunity(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询社区人数最多社区列表")
    @GetMapping("/queryMemberCommunity")
    public R queryMemberCommunity(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                        @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                        @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityService.queryMemberCommunity(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询社区社区最多社区列表")
    @GetMapping("/queryArticleCommunity")
    public R queryArticleCommunity(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCommunityService.queryArticleCommunity(currentPage, pageSize, keyword);
    }
}

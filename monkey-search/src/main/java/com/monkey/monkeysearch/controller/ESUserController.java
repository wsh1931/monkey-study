package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/14 9:34
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch用户模块")
@RestController
@RequestMapping("/monkey-search/user")
public class ESUserController {
    @Resource
    private ESUserService esUserService;

    @ApiOperation("查询所有用户文档")
    @GetMapping("/queryAllUserDocument")
    public R queryAllUserDocument() {
        return esUserService.queryAllUserDocument();
    }

    @ApiOperation("删除所有用户文档")
    @DeleteMapping("/deleteAllUserDocument")
    public R deleteAllUserDocument() {
        return esUserService.deleteAllUserDocument();
    }
    @ApiOperation("将用户数据库中所有数据存入elasticsearch用户文档中")
    @PostMapping("/insertUserDocument")
    public R insertUserDocument() {
        return esUserService.insertUserDocument();
    }

    @ApiOperation("查询综合用户列表")
    @GetMapping("/queryComprehensiveUser")
    public R queryComprehensiveUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryComprehensiveUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询作品数最多用户列表")
    @GetMapping("/queryOpusUser")
    public R queryOpusUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryOpusUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多用户列表")
    @GetMapping("/queryCollectUser")
    public R queryCollectUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryCollectUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询粉丝数最多用户列表")
    @GetMapping("/queryFansUser")
    public R queryFansUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryFansUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞数最多用户列表")
    @GetMapping("/queryLikeUser")
    public R queryLikeUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryLikeUser(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询游览数最多用户列表")
    @GetMapping("/queryViewUser")
    public R queryViewUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryViewUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热用户列表")
    @GetMapping("/queryHireUser")
    public R queryHireUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryHireUser(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新用户列表")
    @GetMapping("/queryLatestUser")
    public R queryLatestUser(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esUserService.queryLatestUser(currentPage, pageSize, keyword);
    }
}

package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/15 10:32
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户功能调用搜索模块feign接口")
@RestController
@RequestMapping("/monkey-search/user/feign")
public class UserFeignController {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("查询用户原文数，游览数，点赞数，收藏数")
    @GetMapping("/queryUserAchievement")
    public R queryUserAchievement() {
        return userFeignService.queryUserAchievement();
    }

    @ApiOperation("得到作者信息")
    @GetMapping("/getAuthorInfoById")
    public R getAuthorInfoById(@RequestParam("authorId") @ApiParam("作者id") Long authorId) {
        return userFeignService.getAuthorInfoById(authorId);
    }

    @ApiOperation("用户游览数 + 1")
    @PutMapping("/userViewAddOne")
    public R userViewAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userViewAddOne(userId);
    }

    @ApiOperation("用户作品数 + 1")
    @PutMapping("/userOpusCountAddOne")
    public R userOpusCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userOpusCountAddOne(userId);
    }

    @ApiOperation("用户作品数 - 1")
    @PutMapping("/userOpusCountSubOne")
    public R userOpusCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userOpusCountSubOne(userId);
    }

    @ApiOperation("用户点赞数 + 1")
    @PutMapping("/userLikeCountAddOne")
    public R userLikeCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userLikeCountAddOne(userId);
    }

    @ApiOperation("用户点赞数 - 1")
    @PutMapping("/userLikeCountSubOne")
    public R userLikeCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userLikeCountSubOne(userId);
    }

    @ApiOperation("用户收藏数 + 1")
    @PutMapping("/userCollectCountAddOne")
    public R userCollectCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userCollectCountAddOne(userId);
    }

    @ApiOperation("用户收藏数 - 1")
    @PutMapping("/userCollectCountSubOne")
    public R userCollectCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userCollectCountSubOne(userId);
    }

    @ApiOperation("用户粉丝数 + 1")
    @PutMapping("/userFansCountAddOne")
    public R userFansCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userFansCountAddOne(userId);
    }

    @ApiOperation("用户粉丝数 - 1")
    @PutMapping("/userFansCountSubOne")
    public R userFansCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userFeignService.userFansCountSubOne(userId);
    }
}
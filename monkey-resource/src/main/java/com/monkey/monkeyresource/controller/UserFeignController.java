package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:16
 * @version: 1.0
 * @description:
 */
@RestController
@Api(tags = "用户模块feign接口")
@RequestMapping("/monkey-resource/user/feign")
public class UserFeignController {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("删除用户购买资源记录")
    @DeleteMapping("/deleteUserBuyResource")
    public R deleteUserBuyResource(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return userFeignService.deleteUserBuyResource(userId, resourceId);
    }

    @ApiOperation("资源收藏数 + 1")
    @PutMapping("/resourceCollectCountAddOne/{resourceId}")
    public R resourceCollectCountAddOne(@PathVariable @ApiParam("资源id") Long resourceId) {
        return userFeignService.resourceCollectCountAddOne(resourceId);
    }

    @ApiOperation("资源收藏数 - 1")
    @PutMapping("/resourceCollectCountSubOne/{resourceId}")
    public R resourceCollectCountSubOne(@PathVariable @ApiParam("资源id") Long resourceId) {
        return userFeignService.resourceCollectCountSubOne(resourceId);
    }

    @ApiOperation("通过资源id得到资源信息")
    @GetMapping("/queryResourceById/{resourceId}")
    public R queryResourceById(@PathVariable @ApiParam("资源id") Long resourceId) {
        return userFeignService.queryResourceById(resourceId);
    }

    @ApiOperation("通过资源id,和评论id得到资源信息")
    @GetMapping("/queryResourceAndCommentById")
    public R queryResourceAndCommentById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                               @RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return userFeignService.queryResourceAndCommentById(resourceId, commentId);
    }

    @ApiOperation("通过资源id得到资源作者id")
    @GetMapping("/queryResourceAuthorById")
    public Long queryResourceAuthorById(@RequestParam("associationId") @ApiParam("资源id") Long resourceId) {
        return userFeignService.queryResourceAuthorById(resourceId);
    }
}

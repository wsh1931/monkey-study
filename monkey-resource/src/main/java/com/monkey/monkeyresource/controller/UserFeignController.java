package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

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

    @ApiOperation("得到资源一周发表数")
    @GetMapping("/queryResourceCountRecentlyWeek")
    public R queryResourceCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return userFeignService.queryResourceCountRecentlyWeek(userId);
    }

    @ApiOperation("删除用户购买资源记录")
    @DeleteMapping("/deleteUserBuyResource")
    public R deleteUserBuyResource(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                   @RequestParam("money") @ApiParam("订单金额") Float money,
                                   @RequestParam("payTime") @ApiParam("支付时间")Date payTime) {
        return userFeignService.deleteUserBuyResource(userId, resourceId, money, payTime);
    }

    @ApiOperation("资源收藏数 + 1")
    @PutMapping("/resourceCollectCountAddOne/{resourceId}")
    public R resourceCollectCountAddOne(@PathVariable @ApiParam("资源id") Long resourceId) {
        return userFeignService.resourceCollectCountAddOne(resourceId);
    }

    @ApiOperation("资源收藏数 - 1")
    @PutMapping("/resourceCollectCountSubOne/{resourceId}")
    public R resourceCollectCountSubOne(@PathVariable @ApiParam("资源id") Long resourceId,
                                        @RequestParam("createTime") @ApiParam("收藏资源事件") Date createTime) {
        return userFeignService.resourceCollectCountSubOne(resourceId, createTime);
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

    @ApiOperation("通过资源，评论id得到资源信息")
    @GetMapping("/queryResourceAndCommentInfoById")
    public R queryResourceAndCommentInfoById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                             @RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return userFeignService.queryResourceAndCommentInfoById(resourceId, commentId);
    }
}

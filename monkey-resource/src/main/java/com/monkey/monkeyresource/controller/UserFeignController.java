package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

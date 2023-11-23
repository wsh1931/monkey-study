package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourcePayFinishService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/22 11:45
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源支付完成接口")
@RestController
@RequestMapping("/monkey-resource/pay/finish")
public class ResourcePayFinishController {
    @Resource
    private ResourcePayFinishService resourcePayFinishService;

    @ApiOperation("提交资源评分")
    @PostMapping("/submitResourceScore")
    public R submitResourceScore(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                 @RequestParam("resourceScore") @ApiParam("资源评分") Integer resourceScore) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourcePayFinishService.submitResourceScore(resourceId, userId, resourceScore);
    }

    @ApiOperation("查询用户资源评分")
    @GetMapping("/queryUserResourceScore")
    public R queryUserResourceScore(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourcePayFinishService.queryUserResourceScore(userId, resourceId);
    }
}

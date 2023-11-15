package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserSearchService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/15 10:16
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户搜索功能接口")
@RestController
@RequestMapping("/monkey-user/search")
public class UserSearchController {
    @Resource
    private UserSearchService userSearchService;

    @ApiOperation("关注用户")
    @PutMapping("/concernUser")
    public R concernUser(@RequestParam("concernId") @ApiParam("关注用户id") Long concernId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return userSearchService.concernUser(concernId, userId);
    }

    @ApiOperation("取消关注用户")
    @PutMapping("/cancelConcernUser")
    public R cancelConcernUser(@RequestParam("concernId") @ApiParam("关注用户id") Long concernId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return userSearchService.cancelConcernUser(concernId, userId);
    }
}

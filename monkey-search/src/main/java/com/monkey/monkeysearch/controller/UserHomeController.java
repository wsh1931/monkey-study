package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.UserHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/23 22:40
 * @version: 1.0
 * @description:
 */
@Api("用户主页接口")
@RestController
@RequestMapping("/monkey-search/user/home")
public class UserHomeController {
    @Resource
    private UserHomeService userHomeService;

    @ApiOperation("查询用户成就")
    @GetMapping("/queryUserAchievement")
    public R queryUserAchievement(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return userHomeService.queryUserAchievement(userId);
    }
}

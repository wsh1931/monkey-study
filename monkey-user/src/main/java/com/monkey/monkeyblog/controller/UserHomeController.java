package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "用户主页接口")
@RestController
@RequestMapping("/monkey-user/user/home")
public class UserHomeController {
    @Resource
    private UserHomeService userHomeService;

    @ApiOperation("查询用户信息")
    @GetMapping("/queryUserInfo")
    @PreAuthorize("@commonAuthority.allUser()")
    public R queryUserAchievement(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userHomeService.queryUserAchievement(userId);
    }
}

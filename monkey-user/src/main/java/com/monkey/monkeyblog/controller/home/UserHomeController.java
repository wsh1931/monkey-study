package com.monkey.monkeyblog.controller.home;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.home.UserHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    public R queryUserAchievement(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userHomeService.queryUserAchievement(userId);
    }

    @ApiOperation("查询最近访客列表")
    @GetMapping("/queryLatestVisit")
    public R queryLatestVisit(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return userHomeService.queryLatestVisit(userId);
    }

    @ApiOperation("添加最近用户访问表")
    @PostMapping("/addToRecentUserVisit")
    public R addToRecentUserVisit(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return userHomeService.addToRecentUserVisit(userId);
    }
}

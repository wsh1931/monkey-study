package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserHomeFansService;
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
 * @date: 2023/12/1 11:03
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页粉丝功能接口")
@RestController
@RequestMapping("/monkey-user/home/fans")
public class UserHomeFansController {
    @Resource
    private UserHomeFansService userHomeFansService;

    @ApiOperation("通过用户id查询用户粉丝")
    @GetMapping("/queryUserFansById")
    public R queryUserFansById(@RequestParam("userId") @ApiParam("用户id") String userId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeFansService.queryUserFansById(userId, currentPage, pageSize);
    }
}

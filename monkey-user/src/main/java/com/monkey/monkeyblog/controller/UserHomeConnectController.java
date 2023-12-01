package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserHomeConnectService;
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
 * @date: 2023/12/1 10:05
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页关注接口")
@RestController
@RequestMapping("/monkey-user/home/connect")
public class UserHomeConnectController {
    @Resource
    private UserHomeConnectService userHomeConnectService;

    @ApiOperation("查询用户关注列表")
    @GetMapping("/queryUserConnectById")
    public R queryUserConnectById(@RequestParam("userId") @ApiParam("用户id") String userId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeConnectService.queryUserConnectById(userId, currentPage, pageSize);
    }


}

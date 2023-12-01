package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserHomeCollectService;
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
 * @date: 2023/12/1 12:10
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页收藏接口")
@RestController
@RequestMapping("/monkey-user/home/collect")
public class UserHomeCollectController {
    @Resource
    private UserHomeCollectService userHomeCollectService;

    @ApiOperation("查询用户收藏目录信息")
    @GetMapping("/queryUserCollectContent")
    public R queryUserCollectContent(@RequestParam("userId") @ApiParam("用户id") String userId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeCollectService.queryUserCollectContent(userId, currentPage, pageSize);
    }

    @ApiOperation("查询收藏详细内容")
    @GetMapping("/queryCollectContentDetail")
    public R queryCollectContentDetail(@RequestParam("collectContentId") @ApiParam("收藏目课id") String collectContentId) {
        return userHomeCollectService.queryCollectContentDetail(collectContentId);
    }
}

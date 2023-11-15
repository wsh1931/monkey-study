package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.SearchFeignService;
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
 * @date: 2023/11/14 10:01
 * @version: 1.0
 * @description:
 */
@Api(tags = "search模块调用用户模块feign接口")
@RestController
@RequestMapping("/monkey-user/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("得到所有用户粉丝信息")
    @GetMapping("/queryAllUserFansInfo")
    public R queryAllUserFansInfo() {
        return searchFeignService.queryAllUserFansInfo();
    }

    @ApiOperation("判断当前登录用户与作者是否为粉丝")
    @GetMapping("/judgeIsFans")
    public R judgeIsFans(@RequestParam("userId") @ApiParam("当前登录用户id") String userId,
                         @RequestParam("authorId") @ApiParam("作者id") String authorId) {
        return searchFeignService.judgeIsFans(userId, authorId);
    }
}

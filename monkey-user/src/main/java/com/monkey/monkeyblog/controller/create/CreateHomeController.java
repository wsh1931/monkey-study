package com.monkey.monkeyblog.controller.create;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.create.CreateHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/14 10:11
 * @version: 1.0
 * @description:
 */
@Api(tags = "创作中心首页接口")
@RequestMapping("/monkey-user/create/home")
@RestController
public class CreateHomeController {
    @Resource
    private CreateHomeService createHomeService;

    @ApiOperation("查询用户近期收藏信息")
    @GetMapping("/queryRecentlyCollect")
    public R queryRecentlyCollect() {
        return createHomeService.queryRecentlyCollect();
    }

    @ApiOperation("查询用户近一周原文数")
    @GetMapping("/queryUserOpusInfoRecentWeek")
    public R queryUserOpusInfoRecentWeek() {
        return createHomeService.queryUserOpusInfoRecentWeek();
    }
}

package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.SearchFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/12 17:19
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜素模块调用资源模块接口")
@RestController
@RequestMapping("/monkey-resource/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("查询所有资源")
    @GetMapping("/queryAllResource")
    public R queryAllResource() {
        return searchFeignService.queryAllResource();
    }

    @ApiOperation("得到所有用户所有资源，点赞，收藏，游览数")
    @GetMapping("/queryAllUserResourceInfo")
    public R queryAllUserResourceInfo() {
        return searchFeignService.queryAllUserResourceInfo();
    }
}

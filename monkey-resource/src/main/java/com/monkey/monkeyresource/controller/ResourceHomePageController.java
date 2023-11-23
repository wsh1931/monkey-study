package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourceHomePageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/13 7:46
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源首页接口")
@RestController
@RequestMapping("/monkey-resource/homePage")
public class ResourceHomePageController {
    @Resource
    private ResourceHomePageService resourceHomePageService;

    @ApiOperation("查询全部精选资源")
    @GetMapping("/queryAllCurationResource")
    public R queryAllCurationResource() {
        return resourceHomePageService.queryAllCurationResource();
    }

    @ApiOperation("通过选则标签id得到精选资源")
    @GetMapping("/selectCurationResource")
    public R selectCurationResource(@RequestParam("classificationId") @ApiParam("需要查找的分类标签id") Long classificationId) {
        return resourceHomePageService.selectCurationResource(classificationId);
    }

    @ApiOperation("查询全部下载次数最多资源")
    @GetMapping("/queryAllHottestResource")
    public R queryAllHottestResource() {
        return resourceHomePageService.queryAllHottestResource();
    }

    @ApiOperation("通过选则标签id得到下载次数最多资源")
    @GetMapping("/selectHottestResource")
    public R selectHottestResource(@RequestParam("classificationId") @ApiParam("需要查找的分类标签id") Long classificationId) {
        return resourceHomePageService.selectHottestResource(classificationId);
    }

    @ApiOperation("查询最新资源集合")
    @GetMapping("/queryLatestResource")
    public R queryLatestResource() {
        return resourceHomePageService.queryLatestResource();
    }

    @ApiOperation("查询资源创作用户排行")
    @GetMapping("/queryUserRank")
    public R queryUserRank() {
        return resourceHomePageService.queryUserRank();
    }

    @ApiOperation("资源游览数 + 1")
    @PutMapping("/resourceViewCountAddOne")
    public R resourceViewCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceHomePageService.resourceViewCountAddOne(resourceId);
    }
}

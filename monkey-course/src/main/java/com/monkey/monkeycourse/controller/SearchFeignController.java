package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.SearchFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 10:55
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜索模块调用课程模块接口")
@RestController
@RequestMapping("/monkey-course/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("查询所有课程")
    @GetMapping("/queryAllCourse")
    public R queryAllCourse() {
        return searchFeignService.queryAllCourse();
    }
}

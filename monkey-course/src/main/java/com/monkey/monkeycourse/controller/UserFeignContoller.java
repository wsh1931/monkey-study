package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/5 15:00
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程模块调用用户模块feign")
@RestController
@RequestMapping("/monkey-course/user/feign")
public class UserFeignContoller {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("课程游览数 + 1")
    @PutMapping("/addCourseViewSum/{courseId}")
    public R addCourseViewSum(@PathVariable Long courseId) {
        return userFeignService.addCourseViewSum(courseId);
    }

    @ApiOperation("课程游览数 - 1")
    @PutMapping("/subCourseViewSum/{courseId}")
    public R subCourseViewSum(@PathVariable Long courseId) {
        return userFeignService.subCourseViewSum(courseId);
    }
}

package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseContentManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/22 20:58
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程内容管理接口")
@RequestMapping("/monkey-course/content/manage")
public class CourseContentManageController {
    @Resource
    private CourseContentManageService courseContentManageService;

    @ApiOperation("查询形式类型列表")
    @GetMapping("/queryFormType")
    public R queryFormType() {
        return courseContentManageService.queryFormType();
    }
}

package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.UserHomeCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 10:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页调用课程模块接口")
@RestController
@RequestMapping("/monkey-course/user/home")
public class UserHomeCourseController {
    @Resource
    private UserHomeCourseService userHomeCourseService;

    @ApiOperation("通过用户id查询课程集合")
    @GetMapping("/queryCourseByUserId")
    public R queryCourseByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                 @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeCourseService.queryCourseByUserId(userId, currentPage, pageSize);
    }

}

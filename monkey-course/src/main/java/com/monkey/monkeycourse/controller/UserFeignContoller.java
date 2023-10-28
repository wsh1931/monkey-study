package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("删除用户购买课程记录")
    @DeleteMapping("/deleteUserBuyCourse")
    public R deleteUserBuyCourse(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return userFeignService.deleteUserBuyCourse(userId, courseId);
    }

    @ApiOperation("通过课程id得到课程信息")
    @GetMapping("/queryCourseById/{courseId}")
    public R queryCourseById(@PathVariable @ApiParam("课程id") Long courseId) {
        return userFeignService.queryCourseById(courseId);
    }

    @ApiOperation("通过课程id, 评论id得到课程信息")
    @GetMapping("/queryCourseAndCommentById")
    public R queryCourseAndCommentById(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                             @RequestParam("commentId") @ApiParam("评论id") Long commentId) {
        return userFeignService.queryCourseAndCommentById(courseId, commentId);
    }
}

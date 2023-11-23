package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseDetailService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:11
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程详情页面接口")
@RestController
@RequestMapping("/monkey-course/detail")
public class CourseDetailController {
    @Resource
    private CourseDetailService courseDetailService;

    @ApiOperation("通过课程id得到课程信息")
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseDetailService.getCourseInfoByCourseId(courseId);
    }

    @ApiOperation("判断当前登录用户是否收藏该课程")
    @GetMapping("/judgeIsCollect")
    public R judgeIsCollect(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
            @RequestParam("collectType") @ApiParam("课程类型") Integer collectType) {
         String userId = JwtUtil.getUserId();
        return courseDetailService.judgeIsCollect(courseId, userId, collectType);
    }

    @ApiOperation("得到官方推荐课程列表")
    @GetMapping("/getCourseRecommendList")
    public R getCourseRecommendList() {
        return courseDetailService.getCourseRecommendList();
    }

    @ApiOperation("通过课程id得到教师信息")
    @GetMapping("/getUserInfoByCourseId")
    public R getUserInfoByCourseId(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseDetailService.getUserInfoByCourseId(courseId);
    }

    @ApiOperation("通过课程id得到相关课程列表")
    @GetMapping("/getConnectCourseList")
    public R getCourseRecommentList(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseDetailService.getConnectCourseList(courseId);
    }

    @ApiOperation("课程游览数 + 1")
    @PutMapping("/courseViewAdd")
    public R courseViewAdd(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseDetailService.courseViewAdd(courseId);
    }
}

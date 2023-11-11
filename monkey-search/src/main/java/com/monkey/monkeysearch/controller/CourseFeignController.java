package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.CourseFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 14:27
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程模块调用搜索模块feign接口")
@RestController
@RequestMapping("/monkey-search/course/feign")
public class CourseFeignController {
    @Resource
    private CourseFeignService courseFeignService;
    @ApiOperation("课程游览数 + 1")
    @PutMapping("/courseViewAddOne")
    public R courseViewAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseViewAddOne(courseId);
    }

    @ApiOperation("课程评论数 + 1")
    @PutMapping("/courseCommentCountAdd")
    public R courseCommentCountAdd(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseCommentCountAdd(courseId);
    }

    @ApiOperation("课程评论数减去对应值")
    @PutMapping("/courseCommentSub")
    public R courseCommentSub(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                              @RequestParam("sum") @ApiParam("减去的数目") Long sum) {
        return courseFeignService.courseCommentSub(courseId, sum);
    }

    @ApiOperation("课程收藏数 + 1")
    @PutMapping("/courseCollectCountAddOne")
    public R courseCollectCountAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseCollectCountAddOne(courseId);
    }
    @ApiOperation("课程收藏数 - 1")
    @PutMapping("/courseCollectCountSubOne")
    public R courseCollectCountSubOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseCollectCountSubOne(courseId);
    }

    @ApiOperation("课程学习人数 + 1")
    @PutMapping("/courseStudyCountAddOne")
    public R courseStudyCountAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseStudyCountAddOne(courseId);
    }

    @ApiOperation("课程学习人数 - 1")
    @PutMapping("/courseStudyCountSubOne")
    public R courseStudyCountSubOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseFeignService.courseStudyCountSubOne(courseId);
    }


    @ApiOperation("更新课程评分")
    @PutMapping("/updateCourseScore")
    public R updateCourseScore(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                               @RequestParam("score") @ApiParam("课程评分") Float score) {
        return courseFeignService.updateCourseScore(courseId, score);
    }
}

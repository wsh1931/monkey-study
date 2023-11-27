package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 11:15
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch课程模块")
@RestController
@RequestMapping("/monkey-search/course")
public class ESCourseController {
    @Resource
    private ESCourseService esCourseService;

    @ApiOperation("查询所有课程文档")
    @GetMapping("/queryAllCourseDocument")
    public R queryAllCourseDocument() {
        return esCourseService.queryAllCourseDocument();
    }

    @ApiOperation("删除所有课程文档")
    @DeleteMapping("/deleteAllCourseDocument")
    public R deleteAllCourseDocument() {
        return esCourseService.deleteAllCourseDocument();
    }

    @ApiOperation("将课程数据库中所有数据存入elasticsearch课程文档中")
    @PostMapping("/insertCourseDocument")
    public R insertCourseDocument() {
        return esCourseService.insertCourseDocument();
    }

    @ApiOperation("查询综合课程列表")
    @GetMapping("/queryComprehensiveCourse")
    public R queryComprehensiveCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                        @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                        @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryComprehensiveCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询评论最多课程列表")
    @GetMapping("/queryCommentCourse")
    public R queryCommentCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryCommentCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多课程列表")
    @GetMapping("/queryCollectCourse")
    public R queryCollectCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                  @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryCollectCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询游览数最多课程列表")
    @GetMapping("/queryViewCourse")
    public R queryViewCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryViewCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热课程列表")
    @GetMapping("/queryHireCourse")
    public R queryHireCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryHireCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新课程列表")
    @GetMapping("/queryLatestCourse")
    public R queryLatestCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryLatestCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询课程评分最多的课程列表")
    @GetMapping("/queryScoreCourse")
    public R queryScoreCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryScoreCourse(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询学习人数最多课程列表")
    @GetMapping("/queryStudyCourse")
    public R queryStudyCourse(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esCourseService.queryStudyCourse(currentPage, pageSize, keyword);
    }
}

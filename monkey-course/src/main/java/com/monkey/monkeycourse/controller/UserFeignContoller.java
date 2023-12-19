package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

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

    @ApiOperation("得到课程一周发表数")
    @GetMapping("/queryCourseCountRecentlyWeek")
    public R queryCourseCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId) {
        return userFeignService.queryCourseCountRecentlyWeek(userId);
    }

    @ApiOperation("课程收藏数 + 1")
    @PutMapping("/courseCollectAddOne/{courseId}")
    public R courseCollectAddOne(@PathVariable Long courseId) {
        return userFeignService.courseCollectAddOne(courseId);
    }

    @ApiOperation("课程收藏数 - 1")
    @PutMapping("/courseCollectSubOne/{courseId}")
    public R courseCollectSubOne(@PathVariable Long courseId,
                                 @RequestParam("createTime") Date createTime) {
        return userFeignService.courseCollectSubOne(courseId, createTime);
    }

    @ApiOperation("删除用户购买课程记录")
    @DeleteMapping("/deleteUserBuyCourse")
    public R deleteUserBuyCourse(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                 @RequestParam("courseId") @ApiParam("课程id") Long courseId,
                                 @RequestParam("money") @ApiParam("订单金额") Float money,
                                 @RequestParam("payTime") @ApiParam("支付时间")Date payTime) {
        return userFeignService.deleteUserBuyCourse(userId, courseId, money, payTime);
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

    @ApiOperation("通过课程id得到课程作者id")
    @GetMapping("/queryCourseAuthorById")
    public Long queryCourseAuthorById(@RequestParam("associationId") @ApiParam("课程id") Long courseId) {
        return userFeignService.queryCourseAuthorById(courseId);
    }
}

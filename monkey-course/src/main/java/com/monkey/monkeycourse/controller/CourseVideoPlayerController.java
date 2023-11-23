package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseVideoPlayerService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/10 14:19
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程视频播放接口")
@RestController
@RequestMapping("/monkey-course/video/player")
public class CourseVideoPlayerController {
    @Resource
    private CourseVideoPlayerService courseVideoPlayerService;

    @ApiOperation("通过课程id得到课程基本信息")
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseVideoPlayerService.getCourseInfoByCourseId(courseId);
    }

    @ApiOperation("通过课程id得到课程目录")
    @GetMapping("/getCourseDirectoryByCourseId")
    public R getCourseDirectoryByCourseId(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.getCourseDirectoryByCourseId(courseId, userId);
    }

    @ApiOperation("用过课程视频id得到课程弹幕列表, 并格式化发送时间")
    @GetMapping("/getBarrageListByCourseVideoId")
    public R getBarrageListByCourseVideoId(@RequestParam("courseVideoId") @ApiParam("课程视频id") Long courseVideoId) {
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.getBarrageListByCourseVideoId(userId, courseVideoId);
    }

    @ApiOperation("撤回2分钟内的弹幕")
    @PutMapping("/cancelBarrage")
    public R cancelBarrage(@RequestParam("barrageId") @ApiParam("弹幕id") Long barrageId) {
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.cancelBarrage(barrageId, userId);
    }

    @ApiOperation("得到课程评价信息")
    @GetMapping("/getCourseScoreInfo")
    public R getCourseScoreInfo(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseVideoPlayerService.getCourseScoreInfo(courseId);
    }

    @ApiOperation("得到评价用户集合")
    @GetMapping("/getCourseScoreUserList")
    public R getCourseScoreUserList(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                                    @RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        return courseVideoPlayerService.getCourseScoreUserList(courseId, currentPage, pageSize);
    }

    @ApiOperation("得到课程作者基本信息")
    @GetMapping("/getUserInfo")
    public R getUserInfo(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        return courseVideoPlayerService.getUserInfo(userId);
    }

    @ApiOperation("判断当前登录用户是否是作者粉丝")
    @GetMapping("/judgeIsFans")
    public R judgeIsFans(@RequestParam("userId") @ApiParam("用户id") Long userId) {
        String nowUserId = JwtUtil.getUserId();
        return courseVideoPlayerService.judgeIsFans(userId, nowUserId);
    }

    @ApiOperation("得到最热课程列表")
    @GetMapping("/getFireCourseList")
    public R getFireCourseList(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return courseVideoPlayerService.getFireCourseList(courseId);
    }

    @ApiOperation("得到该作者的其他课程")
    @GetMapping("/getTeacherOtherCourse")
    public R getTeacherOtherCourse(@RequestParam("teacherId") @ApiParam("教师id") Long teacherId) {
        return courseVideoPlayerService.getTeacherOtherCourse(teacherId);
    }
}

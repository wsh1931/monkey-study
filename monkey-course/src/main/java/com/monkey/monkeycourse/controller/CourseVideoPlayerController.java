package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseVideoPlayerService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * @author: wusihao
 * @date: 2023/8/10 14:19
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/video/player")
public class CourseVideoPlayerController {
    @Autowired
    private CourseVideoPlayerService courseVideoPlayerService;

    // 通过课程id得到课程基本信息
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseVideoPlayerService.getCourseInfoByCourseId(courseId);
    }

    // 通过课程id得到课程目录
    @GetMapping("/getCourseDirectoryByCourseId")
    public R getCourseDirectoryByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.getCourseDirectoryByCourseId(courseId, userId);
    }

    // 用过课程视频id得到课程弹幕列表, 并格式化发送时间
    @GetMapping("/getBarrageListByCourseVideoId")
    public R getBarrageListByCourseVideoId(@RequestParam Map<String, String> data) {
        long courseVideoId = Long.parseLong(data.get("courseVideoId"));
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.getBarrageListByCourseVideoId(userId, courseVideoId);
    }

    // 撤回2分钟内的弹幕
    @PutMapping("/cancelBarrage")
    public R cancelBarrage(@RequestParam Map<String, String> data) {
        long barrageId = Long.parseLong(data.get("barrageId"));
        String userId = JwtUtil.getUserId();
        return courseVideoPlayerService.cancelBarrage(barrageId, userId);
    }

    // 得到课程评价信息
    @GetMapping("/getCourseScoreInfo")
    public R getCourseScoreInfo(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseVideoPlayerService.getCourseScoreInfo(courseId);
    }

    // 得到评价用户集合
    @GetMapping("/getCourseScoreUserList")
    public R getCourseScoreUserList(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return courseVideoPlayerService.getCourseScoreUserList(courseId, currentPage, pageSize);
    }

    // 得到课程作者基本信息
    @GetMapping("/getUserInfo")
    public R getUserInfo(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        return courseVideoPlayerService.getUserInfo(userId);
    }

    // 判断当前登录用户是否是作者粉丝
    @GetMapping("/judgeIsFans")
    public R judgeIsFans(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        String nowUserId = JwtUtil.getUserId();
        return courseVideoPlayerService.judgeIsFans(userId, nowUserId);
    }

    // 得到最热课程列表
    @GetMapping("/getFireCourseList")
    public R getFireCourseList(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseVideoPlayerService.getFireCourseList(courseId);
    }

    // 得到该作者的其他课程
    @GetMapping("/getTeacherOtherCourse")
    public R getTeacherOtherCourse(@RequestParam Map<String, String> data) {
        long teacherId = Long.parseLong(data.get("teacherId"));
        return courseVideoPlayerService.getTeacherOtherCourse(teacherId);
    }
}

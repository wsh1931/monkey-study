package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseDetailService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.jws.HandlerChain;
import javax.swing.plaf.PanelUI;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:11
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/detail")
public class CourseDetailController {
    @Autowired
    private CourseDetailService courseDetailService;

    // 通过课程id得到课程信息
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseDetailService.getCourseInfoByCourseId(courseId);
    }

    // 判断当前登录用户是否收藏该课程
    @GetMapping("/judgeIsCollect")
    public R judgeIsCollect(@RequestParam Map<String, String> data) {
         String userId = JwtUtil.getUserId();
        long courseId = Long.parseLong(data.get("courseId"));
        int collectType = Integer.parseInt(data.get("collectType"));
        return courseDetailService.judgeIsCollect(courseId, userId, collectType);
    }

    // 得到官方推荐课程列表
    @GetMapping("/getCourseRecommendList")
    public R getCourseRecommendList() {
        return courseDetailService.getCourseRecommendList();
    }

    // 通过课程id得到教师信息
    @GetMapping("/getUserInfoByCourseId")
    public R getUserInfoByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseDetailService.getUserInfoByCourseId(courseId);
    }

    // 通过课程id得到相关课程列表
    @GetMapping("/getConnectCourseList")
    public R getCourseRecommentList(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseDetailService.getConnectCourseList(courseId);
    }

    // 课程游览数 + 1
    @PutMapping("/courseViewAdd")
    public R courseViewAdd(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseDetailService.courseViewAdd(courseId);
    }
}

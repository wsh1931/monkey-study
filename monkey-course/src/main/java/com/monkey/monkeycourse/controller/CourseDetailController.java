package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:11
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/course/detail")
public class CourseDetailController {
    @Autowired
    private CourseDetailService courseDetailService;

    // 通过课程id得到课程信息
    @GetMapping("/getCourseInfoByCourseId")
    private R getCourseInfoByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        return courseDetailService.getCourseInfoByCourseId(courseId);
    }
}

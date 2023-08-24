package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseBuyService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/23 15:04
 * @version: 1.0
 * @description: 课程支付
 */
@RestController
@RequestMapping("/monkey-course/pay")
public class CourseBuyController {
    @Autowired
    private CourseBuyService courseBuyService;

    // 通过课程id得到课程信息
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        long userId = Long.parseLong(JwtUtil.getUserId());
        return courseBuyService.getCourseInfoByCourseId(courseId, userId);
    }

}

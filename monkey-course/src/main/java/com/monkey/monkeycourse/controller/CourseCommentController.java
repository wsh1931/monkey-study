package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseCommentService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/6 21:23
 * @version: 1.0
 * @description: 课程评论
 */
@RestController
@RequestMapping("/monkey-course/comment")
public class CourseCommentController {
    @Autowired
    private CourseCommentService courseCommentService;

    // 得到课程评论列表
    @GetMapping("/getCourseCommentList")
    public R getCourseCommentList(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        String userId = JwtUtil.getUserId();
        return courseCommentService.getCourseCommentList(courseId, userId);
    }

    // 发表课程评论
    @GetMapping("/publishCourseComment")
    public R publishCourseComment(@RequestParam Map<String, String> data) {
        String content = data.get("content");
        Long senderId = Long.parseLong(JwtUtil.getUserId());
        long courseId = Long.parseLong(data.get("courseId"));
        return courseCommentService.publishCourseComment(courseId, senderId, content);
    }

    // 课程评论点赞
    @PutMapping("/likeCourseComment")
    public R likeCourseComment(@RequestParam Map<String, String> data) {
        long courseCommentId = Long.parseLong(data.get("courseCommentId"));
        long userId = Long.parseLong(JwtUtil.getUserId());
        return courseCommentService.likeCourseComment(courseCommentId, userId);
    }

    // 课程评论回复功能实现
    @PostMapping("/replyCourseComment")
    public R replyCourseComment(@RequestParam Map<String, String> data) {
        long senderId = Long.parseLong(data.get("senderId"));
        long replyId = Long.parseLong(JwtUtil.getUserId());
        String replyContent = data.get("replyContent");
        long courseCommentId = Long.parseLong(data.get("courseCommentId"));
        long courseId = Long.parseLong(data.get("courseId"));
        return courseCommentService.replyCourseComment(senderId, replyId, replyContent, courseCommentId, courseId);
    }

    // 删除课程评论
    @DeleteMapping("/deleteCourseComment")
    public R deleteCourseComment(@RequestParam Map<String, String> data) {
        String userId = JwtUtil.getUserId();
        Long courseCommentId = Long.parseLong(data.get("courseCommentId"));
        Long parentId = Long.parseLong(data.get("parentId"));
        return courseCommentService.deleteCourseComment(userId, courseCommentId, parentId);
    }

    // 查找未回复课程评论列表
    @GetMapping("/getUnReplyCourseComment")
    public R getUnReplyCourseComment(@RequestParam Map<String, String> data) {
        long courseId = Long.parseLong(data.get("courseId"));
        String userId = JwtUtil.getUserId();
        return courseCommentService.getUnReplyCourseComment(courseId, userId);
    }

    // 得到时间评论降序/升序课程评论列表(type == 0为默认排序, type == 1为降序，type == 2为升序)
    @GetMapping("/getDownOrUpgradeCourseComment")
    public R getDownOrUpgradeCourseComment(@RequestParam Map<String, String> data) {
        int type = Integer.parseInt(data.get("type"));
        String userId = JwtUtil.getUserId();
        long courseId = Long.parseLong(data.get("courseId"));
        return courseCommentService.getDownOrUpgradeCourseComment(type, userId, courseId);
    }
}

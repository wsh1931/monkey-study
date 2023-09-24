package com.monkey.monkeycourse.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.pojo.CourseComment;
import com.monkey.monkeycourse.service.CourseCommentService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/6 21:23
 * @version: 1.0
 * @description: 课程评论
 */
@RestController
@RequestMapping("/monkey-course/comment")
@Api(tags = "课程评论后端接口")
public class CourseCommentController {
    @Resource
    private CourseCommentService courseCommentService;

    @ApiOperation("得到课程评论列表")
    @GetMapping("/getCourseCommentList")
    public R getCourseCommentList(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return courseCommentService.getCourseCommentList(courseId, userId, currentPage, pageSize);
    }

    @ApiOperation("发表课程评论")
    @GetMapping("/publishCourseComment")
    public R publishCourseComment(@RequestParam("content") @ApiParam("发布课程内容") String content,
                                  @RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        Long senderId = Long.parseLong(JwtUtil.getUserId());
        return courseCommentService.publishCourseComment(courseId, senderId, content);
    }

    @ApiOperation("课程评论点赞")
    @PutMapping("/likeCourseComment")
    public R likeCourseComment(@RequestParam("courseCommentId") @ApiParam("课程评论id")Long courseCommentId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return courseCommentService.likeCourseComment(courseCommentId, userId);
    }

    @ApiOperation("课程评论回复功能实现")
    @PostMapping("/replyCourseComment")
    public R replyCourseComment(@RequestParam("senderId") @ApiParam("接收者评论id")Long senderId,
                                @RequestParam("courseCommentId") @ApiParam("课程评论id")Long courseCommentId,
                                @RequestParam("courseId") @ApiParam("课程id")Long courseId,
                                @RequestParam("replyContent") @ApiParam("回复内容")String  replyContent
                                ) {
        long replyId = Long.parseLong(JwtUtil.getUserId());
        return courseCommentService.replyCourseComment(senderId, replyId, replyContent, courseCommentId, courseId);
    }

    @ApiOperation("删除课程评论")
    @DeleteMapping("/deleteCourseComment")
    public R deleteCourseComment(@RequestParam("courseCommentId") @ApiParam("课程评论id") Long courseCommentId,
                                 @RequestParam("parentId") @ApiParam("课程评论父id") Long parentId,
                                 @RequestParam("courseId") @ApiParam("课程id")Long courseId) {
        return courseCommentService.deleteCourseComment(courseCommentId, parentId, courseId);
    }

    @ApiOperation("查找未回复课程评论列表")
    @GetMapping("/getUnReplyCourseComment")
    public R getUnReplyCourseComment(@RequestParam("courseId") @ApiParam("课程id")Long courseId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return courseCommentService.getUnReplyCourseComment(courseId, userId, currentPage, pageSize);
    }

    @ApiOperation("得到时间评论降序/升序课程评论列表(type == 0为默认排序, type == 1为降序，type == 2为升序)")
    @GetMapping("/getDownOrUpgradeCourseComment")
    public R getDownOrUpgradeCourseComment(@RequestParam("courseId") @ApiParam("课程id")Long courseId,
                                           @RequestParam("type") @ApiParam("类型")Integer type,
                                           @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String userId = JwtUtil.getUserId();
        return courseCommentService.getDownOrUpgradeCourseComment(type, userId, courseId, currentPage, pageSize);
    }

    @ApiOperation("判断当前课程用户是否是课程作者")
    @GetMapping("/judgeIsAuthor")
    public R judgeIsAuthor(@RequestParam("courseId") @ApiParam("课程id")Long courseId) {
        String userId = JwtUtil.getUserId();
        return courseCommentService.judgeIsAuthor(courseId, userId);
    }

    @ApiOperation("精选课程评论")
    @PutMapping("/excellentSelect")
    public R excellentSelect(@RequestParam("courseComment") String courseCommentStr) {
        CourseComment courseComment = JSONObject.parseObject(courseCommentStr, CourseComment.class);
        return courseCommentService.excellentSelect(courseComment);
    }
}

package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.Date;

@FeignClient(value = "monkey-course", contextId = "user-to-course")
public interface UserToCourseFeignService {

    // 得到课程一周发表数
    @GetMapping("/monkey-course/user/feign/queryCourseCountRecentlyWeek")
    R queryCourseCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId);

    // 课程收藏数 + 1
    @PutMapping("/monkey-course/user/feign/courseCollectAddOne/{courseId}")
    R courseCollectAddOne(@PathVariable Long courseId);

    // 课程收藏数 - 1
    @PutMapping("/monkey-course/user/feign/courseCollectSubOne/{courseId}")
    R courseCollectSubOne(@PathVariable Long courseId,
                          @RequestParam("createTime") Date createTime);

    @DeleteMapping("/monkey-course/user/feign/deleteUserBuyCourse")
    R deleteUserBuyCourse(@RequestParam("userId") @ApiParam("用户id") Long userId,
                          @RequestParam("courseId") @ApiParam("课程id") Long courseId,
                          @RequestParam("money") @ApiParam("订单金额") Float money,
                          @RequestParam("payTime") @ApiParam("支付时间")Date payTime);

    @GetMapping("/monkey-course/user/feign/queryCourseById/{courseId}")
    R queryCourseById(@PathVariable @ApiParam("课程id") Long courseId);

    @GetMapping("/monkey-course/user/feign/queryCourseAndCommentById")
    R queryCourseAndCommentById(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                                       @RequestParam("commentId") @ApiParam("评论id") Long commentId);

    @GetMapping("/monkey-course/user/feign/queryCourseAuthorById")
    Long queryCourseAuthorById(@RequestParam("associationId") @ApiParam("课程id") Long courseId);
}

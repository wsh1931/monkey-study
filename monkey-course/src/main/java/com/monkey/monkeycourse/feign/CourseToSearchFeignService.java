package com.monkey.monkeycourse.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "course-to-search")
public interface CourseToSearchFeignService {
    // 课程游览数 + 1
    @PutMapping("/monkey-search/course/feign/courseViewAddOne")
    R courseViewAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    // 课程评论数 + 1
    @PutMapping("/monkey-search/course/feign/courseCommentCountAdd")
    R courseCommentCountAdd(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    @PutMapping("/monkey-search/course/feign/courseCommentSub")
    R courseCommentSub(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                              @RequestParam("sum") @ApiParam("减去的数目") Long sum);

    // 课程收藏数 + 1
    @PutMapping("/monkey-search/course/feign/courseCollectCountAddOne")
    R courseCollectCountAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    // 课程收藏数 - 1
    @PutMapping("/monkey-search/course/feign/courseCollectCountSubOne")
    R courseCollectCountSubOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    // 课程学习人数 + 1
    @PutMapping("/monkey-search/course/feign/courseStudyCountAddOne")
    R courseStudyCountAddOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    // 课程学习人数 - 1
    @PutMapping("/monkey-search/course/feign/courseStudyCountSubOne")
    R courseStudyCountSubOne(@RequestParam("courseId") @ApiParam("课程id") Long courseId);

    // 更新课程评分
    @PutMapping("/monkey-search/course/feign/updateCourseScore")
    R updateCourseScore(@RequestParam("courseId") @ApiParam("课程id") Long courseId,
                               @RequestParam("score") @ApiParam("课程评分") Float score);

    // 用户游览数 + 1
    @PutMapping("/monkey-search/user/feign/userViewAddOne")
    R userViewAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户作品数 + 1
    @PutMapping("/monkey-search/user/feign/userOpusCountAddOne")
    R userOpusCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);


    // 用户作品数 - 1
    @PutMapping("/monkey-search/user/feign/userOpusCountSubOne")
    R userOpusCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 + 1
    @PutMapping("/monkey-search/user/feign/userLikeCountAddOne")
    R userLikeCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 - 1
    @PutMapping("/monkey-search/user/feign/userLikeCountSubOne")
    R userLikeCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 + 1
    @PutMapping("/monkey-search/user/feign/userCollectCountAddOne")
    R userCollectCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 - 1
    @PutMapping("/monkey-search/user/feign/userCollectCountSubOne")
    R userCollectCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);
}

package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-course", contextId = "user-to-course")
public interface UserToCourseFeignService {

    // 课程游览数 + 1
    @PutMapping("/monkey-course/user/feign/addCourseViewSum/{courseId}")
    R addCourseViewSum(@PathVariable Long courseId);

    // 课程游览数 - 1
    @PutMapping("/monkey-course/user/feign/subCourseViewSum/{courseId}")
    R subCourseViewSum(@PathVariable Long courseId);

    @DeleteMapping("/monkey-course/user/feign/deleteUserBuyCourse")
    R deleteUserBuyCourse(@RequestParam("userId") @ApiParam("用户id") Long userId,
                          @RequestParam("courseId") @ApiParam("课程id") Long courseId);
}

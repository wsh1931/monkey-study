package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "monkey-course", contextId = "uset-to-course")
public interface UserToCourseFeignService {

    // 课程游览数 + 1
    @PutMapping("/monkey-course/user/feign/addCourseViewSum/{courseId}")
    R addCourseViewSum(@PathVariable Long courseId);

    // 课程游览数 - 1
    @PutMapping("/monkey-course/user/feign/subCourseViewSum/{courseId}")
    R subCourseViewSum(@PathVariable Long courseId);
}

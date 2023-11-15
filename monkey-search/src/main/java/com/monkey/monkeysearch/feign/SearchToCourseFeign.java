package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "monkey-course", contextId = "search-to-course")
public interface SearchToCourseFeign {
    // 查询所有课程
    @GetMapping("/monkey-course/search/feign/queryAllCourse")
    R queryAllCourse();

    // 得到所有用户所有课程，点赞，收藏，游览数
    @GetMapping("/monkey-course/search/feign/queryAllUserCourseInfo")
    R queryAllUserCourseInfo();
}

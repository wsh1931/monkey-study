package com.monkey.monkeycourse.authority;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 11:16
 * @version: 1.0
 * @description:
 */
@Component
public class CourseCustomAuthority {
    @Resource
    private CourseMapper courseMapper;

}

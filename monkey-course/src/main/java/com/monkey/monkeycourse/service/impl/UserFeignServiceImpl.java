package com.monkey.monkeycourse.service.impl;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.service.UserFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/8/5 15:05
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Autowired
    private CourseMapper courseMapper;
    /**
     * 课程游览数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:07
     */
    @Override
    public R addCourseViewSum(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        course.setCollectCount(course.getCollectCount() + 1);
        return R.ok(courseMapper.updateById(course));
    }

    /**
     * 课程游览数 - 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:08
     */
    @Override
    public R subCourseViewSum(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        course.setCollectCount(course.getCollectCount() - 1);
        return R.ok(courseMapper.updateById(course));
    }
}

package com.monkey.monkeycourse.service.impl;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.Vo.CourseDetailVo;
import com.monkey.monkeycourse.service.CourseDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:11
 * @version: 1.0
 * @description:
 */
@Service
public class CourseDetailServiceImpl implements CourseDetailService {
    @Autowired
    private CourseMapper courseMapper;
    /**
     * 通过课程id得到课程信息
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/30 20:21
     */
    @Override
    public R getCourseInfoByCourseId(long courseId) {
        // 最终返回类
        CourseDetailVo courseDetailVo = new CourseDetailVo();
        Course course = courseMapper.selectById(courseId);
        BeanUtils.copyProperties(course, courseDetailVo);

        // 得到课程收藏数

        return null;
    }
}

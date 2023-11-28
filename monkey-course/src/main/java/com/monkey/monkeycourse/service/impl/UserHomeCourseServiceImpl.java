package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.Vo.CourseVo;
import com.monkey.monkeycourse.service.UserHomeCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/28 10:29
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeCourseServiceImpl implements UserHomeCourseService {
    @Resource
    private CourseMapper courseMapper;

    /**
     * 通过用户id查询课程集合
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/28 10:44
     */
    @Override
    public R queryCourseByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getUserId, userId);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = courseMapper.selectPage(page, courseLambdaQueryWrapper);
        List<Course> courseList = selectPage.getRecords();
        List<CourseVo> courseVos = new ArrayList<>();
        courseList.forEach(course -> {
            CourseVo courseVo = new CourseVo();
            BeanUtils.copyProperties(course, courseVo);
            Long formTypeId = course.getFormTypeId();
            FormTypeEnum formTypeEnum = FormTypeEnum.getFormTypeEnum(formTypeId);
            courseVo.setFormTypeName(formTypeEnum.getMsg());
            courseVos.add(courseVo);
        });

        selectPage.setRecords(courseVos);
        return R.ok(selectPage);
    }

}

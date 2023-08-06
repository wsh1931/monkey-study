package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.mapper.TeacherMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.Teacher;
import com.monkey.monkeycourse.pojo.Vo.CourseCardVo;
import com.monkey.monkeycourse.pojo.Vo.CourseDetailVo;
import com.monkey.monkeycourse.service.CourseDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
    @Autowired
    private CollectContentConnectMapper collectContentConnectMapper;
    @Autowired
    private TeacherMapper teacherMapper;
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
        Course course  = courseMapper.selectById(courseId);
        CourseDetailVo courseDetailVo = new CourseDetailVo();
        BeanUtils.copyProperties(course, courseDetailVo);
        // 得到课程价格
        Integer isFree = courseDetailVo.getIsFree();
        if (isFree.equals(CommonEnum.COURSE_UNFREE.getCode())) {
            Float price = courseDetailVo.getCoursePrice();
            Float discount = courseDetailVo.getDiscount();
            Integer isDiscount = courseDetailVo.getIsDiscount();
            if (isDiscount.equals(CommonEnum.COURSE_DISCOUNT.getCode())) {
                String str = String.valueOf(price * discount * 0.1);
                int index = str.indexOf('.');
                if (index != -1 && index + 3 <= str.length()) {
                    courseDetailVo.setDiscountPrice(str.substring(0, index + 3));
                } else {
                    courseDetailVo.setDiscountPrice(str);
                }
            }
        }
        return R.ok(courseDetailVo);
    }

    /**
     * 判断当前登录用户是否收藏该课程
     *
     * @param courseId 课程id
     * @param userId 当前登录用户id
     * @param collectType 收藏类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 21:27
     */
    @Override
    public R judgeIsCollect(long courseId, String userId, int collectType) {
        QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
        collectContentConnectQueryWrapper.eq("user_id", userId);
        collectContentConnectQueryWrapper.eq("associate_id", courseId);
        collectContentConnectQueryWrapper.eq("type", collectType);
        Long selectCount = collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper);
        if (selectCount > 0) {
            return R.ok(CommonEnum.COLLECT.getCode());
        } else {
            return R.ok(CommonEnum.UNCOLLECT.getCode());
        }
    }

    /**
     * 得到官方推荐课程列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/6 12:03
     */
    @Override
    public R getCourseCommentList() {
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("form_type_id", FormTypeEnum.FORM_TYPE_COMMEND.getCode());
        List<Course> courseList = courseMapper.selectList(courseQueryWrapper);
        List<CourseCardVo> courseCardVoList = new ArrayList<>();
        for (Course course: courseList) {
            CourseCardVo courseCardVo = new CourseCardVo();
            BeanUtils.copyProperties(course, courseCardVo);
            courseCardVo.setId(course.getId());
            courseCardVo.setPicture(course.getPicture());
            courseCardVo.setSectionCount(course.getSectionCount());
            // 通过课程id得到教师名称
            Long teacherId = course.getTeacherId();
            Teacher teacher = teacherMapper.selectById(teacherId);
            courseCardVo.setTeacherName(teacher.getName());
            courseCardVo.setTitle(course.getTitle());

            courseCardVoList.add(courseCardVo);
        }
        return R.ok(courseCardVoList);
    }
}

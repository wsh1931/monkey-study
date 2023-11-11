package com.monkey.monkeycourse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.constant.CourseEnum;
import com.monkey.monkeycourse.feign.CourseToUserFeignService;
import com.monkey.monkeycourse.mapper.CourseLabelMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseLabel;
import com.monkey.monkeycourse.pojo.Vo.CourseCardVo;
import com.monkey.monkeycourse.pojo.Vo.CourseDetailVo;
import com.monkey.monkeycourse.rabbitmq.EventConstant;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycourse.service.CourseDetailService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:11
 * @version: 1.0
 * @description:
 */
@Service
public class CourseDetailServiceImpl implements CourseDetailService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private CourseLabelMapper courseLabelMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CourseToUserFeignService courseToUserFeignService;
    @Resource
    private RabbitTemplate rabbitTemplate;
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
        Long formTypeId = courseDetailVo.getFormTypeId();
        if (!formTypeId.equals(FormTypeEnum.FORM_TYPE_FREE.getCode()) || !formTypeId.equals(FormTypeEnum.FORM_TYPE_COMMEND.getCode())) {
            courseDetailVo.setIsFree(CourseEnum.COURSE_UNFREE.getCode());
            Float price = courseDetailVo.getCoursePrice();
            Float discount = courseDetailVo.getDiscount();
            Integer isDiscount = courseDetailVo.getIsDiscount();
            if (isDiscount.equals(CourseEnum.COURSE_DISCOUNT.getCode())) {
                String str = String.valueOf(price * discount * 0.1);
                int index = str.indexOf('.');
                if (index != -1 && index + 3 <= str.length()) {
                    courseDetailVo.setDiscountPrice(str.substring(0, index + 3));
                } else {
                    courseDetailVo.setDiscountPrice(str);
                }
            }
        } else {
            courseDetailVo.setIsFree(CourseEnum.COURSE_FREE.getCode());
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
    public R getCourseRecommendList() {
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
            // 通过课程id得到用户名称
            Long userId = course.getUserId();
            User user = userMapper.selectById(userId);
            courseCardVo.setUserName(user.getUsername());
            courseCardVo.setTitle(course.getTitle());

            courseCardVoList.add(courseCardVo);
        }
        return R.ok(courseCardVoList);
    }

    /**
     * 通过课程id得到用户信息
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/6 17:39
     */
    @Override
    public R getUserInfoByCourseId(long courseId) {
        Course course = courseMapper.selectById(courseId);
        Long userId = course.getUserId();
        // 通过用户id得到用户关注数和用户粉丝数
        R userConcernAndFansCountByUserId = courseToUserFeignService.getUserConcernAndFansCountByUserId(userId);
        if (userConcernAndFansCountByUserId.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(userConcernAndFansCountByUserId.getCode(), userConcernAndFansCountByUserId.getMsg());
        }

        UserVo userVo = (UserVo)userConcernAndFansCountByUserId.getData(new TypeReference<UserVo>(){});
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(user, userVo);
        return R.ok(userVo);
    }

    /**
     * 通过课程id得到相关课程列表
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/6 17:49
     */
    @Override
    public R getConnectCourseList(long courseId) {
        QueryWrapper<CourseLabel> courseLabelQueryWrapper = new QueryWrapper<>();
        courseLabelQueryWrapper.select("label_id");
        courseLabelQueryWrapper.eq("course_id", courseId);
        List<CourseLabel> courseLabelList = courseLabelMapper.selectList(courseLabelQueryWrapper);
        // 通过标签查找其他课程
        // 查找到的最大课程数不超过10；
        int max_cnt = 0;
        List<CourseCardVo> courseCardVoList = new ArrayList<>();
        for (CourseLabel courseLabel : courseLabelList) {
            Long labelId = courseLabel.getLabelId();
            QueryWrapper<CourseLabel> labelQueryWrapper = new QueryWrapper<>();
            labelQueryWrapper.select("course_id");
            labelQueryWrapper.eq("label_id", labelId);
            List<CourseLabel> courseLabels = courseLabelMapper.selectList(labelQueryWrapper);
            for (CourseLabel label : courseLabels) {
                Long labelCourseId = label.getCourseId();
                if (labelCourseId.equals(courseId)) {
                    continue;
                }
                Course course = courseMapper.selectById(labelCourseId);
                CourseCardVo courseCardVo = new CourseCardVo();
                BeanUtils.copyProperties(course, courseCardVo);
                courseCardVo.setId(course.getId());
                courseCardVo.setPicture(course.getPicture());
                courseCardVo.setSectionCount(course.getSectionCount());
                // 通过课程id得到用户名称
                Long userId = course.getUserId();
                User user = userMapper.selectById(userId);
                courseCardVo.setUserName(user.getUsername());
                courseCardVo.setTitle(course.getTitle());

                courseCardVoList.add(courseCardVo);

                if (++ max_cnt >= 10) {
                    break;
                }
            }

            if (++ max_cnt >= 10) {
                break;
            }
        }
        return R.ok(courseCardVoList);
    }

    /**
     * 课程游览数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/31 17:18
     */
    @Override
    public R courseViewAdd(long courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseViewCountAddOne);
        jsonObject.put("courseId", courseId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                RabbitmqRoutingName.courseUpdateRouting, message);
        return R.ok();
    }
}

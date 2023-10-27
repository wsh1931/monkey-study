package com.monkey.monkeycourse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseBuyMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseBuy;
import com.monkey.monkeycourse.rabbitmq.EventConstant;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycourse.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/5 15:05
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private CourseBuyMapper courseBuyMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseViewCountAddOne);
        jsonObject.put("courseId", courseId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                RabbitmqRoutingName.courseUpdateRouting, message);
        return R.ok(1);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseViewCountSubOne);
        jsonObject.put("courseId", courseId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                RabbitmqRoutingName.courseUpdateRouting, message);
        return R.ok(1);
    }

    /**
     *  删除用户购买课程记录
     *
     * @param userId 用户id
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 16:36
     */
    @Override
    public R deleteUserBuyCourse(Long userId, Long courseId) {
        QueryWrapper<CourseBuy> courseBuyQueryWrapper = new QueryWrapper<>();
        courseBuyQueryWrapper.eq("user_id", userId);
        courseBuyQueryWrapper.eq("course_id", courseId);
        int delete = courseBuyMapper.delete(courseBuyQueryWrapper);
        return R.ok(delete);
    }

    /**
     * 通过课程id得到课程信息
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:36
     */
    @Override
    public R queryCourseById(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("picture", course.getPicture());
        jsonObject.put("title", course.getTitle());
        return R.ok(jsonObject);
    }
}

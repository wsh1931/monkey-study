package com.monkey.monkeycourse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.Article;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseBuyMapper;
import com.monkey.monkeycourse.mapper.CourseCommentMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseBuy;
import com.monkey.monkeycourse.pojo.CourseComment;
import com.monkey.monkeycourse.rabbitmq.EventConstant;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycourse.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @Resource
    private CourseCommentMapper courseCommentMapper;
    /**
     * 课程游览数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:07
     */
    @Override
    public R courseCollectAddOne(Long courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseCollectAddOne);
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
    public R courseCollectSubOne(Long courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseCollectSubOne);
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

    /**
     * 通过课程id, 评论id得到课程信息
     *
     * @param courseId 课程id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 15:14
     */
    @Override
    public R queryCourseAndCommentById(Long courseId, Long commentId) {
        Course course = courseMapper.selectById(courseId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("picture", course.getPicture());
        jsonObject.put("contentTitle", course.getTitle());
        CourseComment courseComment = courseCommentMapper.selectById(commentId);
        jsonObject.put("title", courseComment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 通过课程id得到课程作者id
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/29 21:26
     */
    @Override
    public Long queryCourseAuthorById(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        return course.getUserId();
    }

}

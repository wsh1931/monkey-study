package com.monkey.monkeycourse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonConstant;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.constant.CourseEnum;
import com.monkey.monkeycourse.mapper.CourseCommentLikeMapper;
import com.monkey.monkeycourse.mapper.CourseCommentMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseComment;
import com.monkey.monkeycourse.pojo.CourseCommentLike;
import com.monkey.monkeycourse.pojo.Vo.CourseCommentVo;
import com.monkey.monkeycourse.rabbitmq.EventConstant;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycourse.service.CourseCommentService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/8/6 21:24
 * @version: 1.0
 * @description:
 */
@Service
public class CourseCommentServiceImpl implements CourseCommentService {
    @Resource
    private CourseCommentMapper courseCommentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private CourseCommentLikeMapper courseCommentLikeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 得到课程评论列表(按点赞数降序排序)
     *
     * @param courseId    课程id
     * @param userId      当前登录用户od
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/7 17:27
     */
    @Override
    public R getCourseCommentList(long courseId, String userId, Long currentPage, Long pageSize) {
        // 查询一级评论列表
        QueryWrapper<CourseComment> courseCommentQueryWrapper = new QueryWrapper<>();
        courseCommentQueryWrapper.eq("course_id", courseId);
        courseCommentQueryWrapper.orderByDesc("like_count");
        courseCommentQueryWrapper.le("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        Page page = new Page<>(currentPage, pageSize);

        Page selectPage = courseCommentMapper.selectPage(page, courseCommentQueryWrapper);
        List<CourseComment> oneCourseCommentList = selectPage.getRecords();
        // 通过一级评论得到二, 三级评论
        JSONObject jsonObject = getTwoCommentByOneComment(oneCourseCommentList, userId, courseId);
        Long commentCount = jsonObject.getLong("commentCount");
        List<CourseCommentVo> courseCommentVoList = JSONObject.parseArray(jsonObject.getString("courseCommentVoList"), CourseCommentVo.class);
        selectPage.setRecords(courseCommentVoList);

        JSONObject data = new JSONObject();
        data.put("selectPage", selectPage);
        data.put("commentCount", commentCount);
        return R.ok(data);
    }

    /**
     * 发表课程评论
     *
     * @param courseId 课程id
     * @param senderId 发送评论人id
     * @param content 发表评论内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 9:29
     */
    @Transactional
    @Override
    public R publishCourseComment(long courseId, Long senderId, String content) {
        CourseComment courseComment = new CourseComment();
        courseComment.setCourseId(courseId);
        courseComment.setSenderId(senderId);
        courseComment.setContent(content);
        courseComment.setParentId((long)CommonEnum.ONE_LEVEL_COMMENT.getCode());
        courseComment.setCreateTime(new Date());
        int insert = courseCommentMapper.insert(courseComment);
        if (insert > 0) {
            // 课程评论数 + 1
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.courseCommentCountAddOne);
            jsonObject.put("courseId", courseId);
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                    RabbitmqRoutingName.courseUpdateRouting, message);
            return R.ok(CommonConstant.publishCourseCommentSuccess);
        }
        return R.error(CommonConstant.publishCourseCommentFail);
    }

    /**
     * 课程评论点赞
     *
     * @param courseCommentId 课程评论id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 9:48
     */
    @Override
    public R likeCourseComment(long courseCommentId, long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseCommentLike);
        jsonObject.put("userId", userId);
        jsonObject.put("courseCommentId", courseCommentId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseInsertDirectExchange,
                RabbitmqRoutingName.courseInsertRouting, message);

        return R.ok();
    }

    /**
     * 课程评论回复功能实现
     *
     * @param senderId 发送评论人id
     * @param replyId 回复人id
     * @param replyContent 回复内容
     * @param courseCommentId 课程评论id
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 10:14
     */
    @Override
    public R replyCourseComment(long senderId, long replyId, String replyContent, long courseCommentId, long courseId) {
        CourseComment courseComment = new CourseComment();
        courseComment.setReplyId(replyId);
        courseComment.setSenderId(senderId);
        courseComment.setContent(replyContent);
        courseComment.setCourseId(courseId);
        courseComment.setCreateTime(new Date());
        courseComment.setParentId(courseCommentId);
        courseCommentMapper.insert(courseComment);
        // 课程评论数 + 1
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.courseCommentCountAddOne);
        jsonObject.put("courseId", courseId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                RabbitmqRoutingName.courseUpdateRouting, message);
        return R.ok();
    }

    /**
     * 删除课程评论（若为一级评论则删除以下所有回复，否则只删除当前评论）
     *
     * @param courseCommentId 需要删除的课程评论id
     * @param parentId        该课程评论的父id
     * @param courseId         课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 16:15
     */
    @Override
    public R deleteCourseComment(Long courseCommentId, Long parentId, Long courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.deleteCourseComment);
        jsonObject.put("courseCommentId", courseCommentId);
        jsonObject.put("parentId", parentId);
        jsonObject.put("courseId", courseId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseDeleteDirectExchange,
                RabbitmqRoutingName.courseDeleteRouting, message);

        return R.ok();
    }

    /**
     * 查找未回复课程评论列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 16:24
     */
    @Override
    public R getUnReplyCourseComment(long courseId, String userId, Long currentPage, Long pageSize) {
        // 最终返回集合
        List<CourseCommentVo> courseCommentVoList = new ArrayList<>();
        // 查找未回复的一级评论id
        QueryWrapper<CourseComment> courseCommentQueryWrapper = new QueryWrapper<>();
        courseCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        courseCommentQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = courseCommentMapper.selectPage(page, courseCommentQueryWrapper);
        List<CourseComment> oneCourseCommentList = selectPage.getRecords();
        // 计算评论总数
        long comment_cnt = 0L;

        for (CourseComment oneCourseComment : oneCourseCommentList) {
            // 判断子评论是否存在回复人信息
            boolean existReply = false;
            long one_and_two_comment_count = 1L;
            CourseCommentVo oneCourseCommentVo = new CourseCommentVo();
            BeanUtils.copyProperties(oneCourseComment, oneCourseCommentVo);
            Date commentTime = oneCourseComment.getCreateTime();
            String content = oneCourseComment.getContent();
            Long likeCount = oneCourseComment.getLikeCount();
            Integer isCuration = oneCourseComment.getIsCuration();
            Long senderId = oneCourseComment.getSenderId();
            oneCourseCommentVo.setCommentTime(commentTime);
            oneCourseCommentVo.setContent(content);
            oneCourseCommentVo.setShowInput(CommonEnum.UNSHOW_INPUT.getCode());
            oneCourseCommentVo.setCommentLikeSum(likeCount);
            oneCourseCommentVo.setIsCuration(isCuration);
            oneCourseCommentVo.setSenderId(senderId);
            oneCourseCommentVo.setId(oneCourseComment.getId());
            oneCourseCommentVo.setIsShowMoreContent(CommonEnum.UNSHOW_MORE.getCode());
            oneCourseCommentVo.setIsShowMore(CommonEnum.UNSHOW_MORE.getCode());
            oneCourseCommentVo.setIsCuration(oneCourseComment.getIsCuration());
            // 通过senderId得到发送用户头像姓名
            User user = userMapper.selectById(senderId);
            oneCourseCommentVo.setSenderName(user.getUsername());
            oneCourseCommentVo.setSenderPhoto(user.getPhoto());


            if (userId != null && !"".equals(userId)) {
                // 判断当前登录用户是否点赞该评论
                QueryWrapper<CourseCommentLike> courseCommentLikeQueryWrapper = new QueryWrapper<>();
                courseCommentLikeQueryWrapper.eq("user_id", userId);
                courseCommentLikeQueryWrapper.eq("course_comment_id", oneCourseCommentVo.getId());
                Long selectCount = courseCommentLikeMapper.selectCount(courseCommentLikeQueryWrapper);
                oneCourseCommentVo.setIsLike(selectCount);

                // 判断是否为当前登录用户发表的评论
                if (oneCourseComment.getSenderId().equals(Long.parseLong(userId))) {
                    oneCourseCommentVo.setCommentIsOfLoginUser(CommonEnum.LOGIN_USER_COMMENT.getCode());
                } else {
                    oneCourseCommentVo.setCommentIsOfLoginUser(CommonEnum.NOT_LOGIN_USER_COMMENT.getCode());
                }
            }

            Long courseCommentVoId = oneCourseCommentVo.getId();
            // 通过一级评论id找到二，三级评论信息
            QueryWrapper<CourseComment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("parent_id", courseCommentVoId);
            commentQueryWrapper.eq("course_id", courseId);
            List<CourseComment> twoCourseCommentList = courseCommentMapper.selectList(commentQueryWrapper);

            // 子评论集合
            List<CourseCommentVo> twoList = new ArrayList<>();
            for (CourseComment twoCourseComment : twoCourseCommentList) {
                if (userId != null && !"".equals(userId)) {
                    if (twoCourseComment.getReplyId().equals(Long.parseLong(userId))) {
                        existReply = true;
                        break;
                    }
                }

                one_and_two_comment_count ++ ;
                CourseCommentVo twoCourseCommentvo = new CourseCommentVo();
                BeanUtils.copyProperties(twoCourseComment, twoCourseCommentvo);
                String twoContent = twoCourseComment.getContent();
                Long twosenderId = twoCourseComment.getSenderId();
                Long twoReplyId = twoCourseComment.getReplyId();
                Date twocCommentTime = twoCourseComment.getCreateTime();
                Long twolikeCount = twoCourseComment.getLikeCount();
                twoCourseCommentvo.setContent(twoContent);
                twoCourseCommentvo.setCommentTime(twocCommentTime);
                twoCourseCommentvo.setCommentLikeSum(twolikeCount);

                // 得到发送者回复者信息
                User twoSender = userMapper.selectById(twosenderId);
                User twoReply = userMapper.selectById(twoReplyId);
                twoCourseCommentvo.setSenderId(twoSender.getId());
                twoCourseCommentvo.setSenderName(twoSender.getUsername());
                twoCourseCommentvo.setSenderPhoto(twoSender.getPhoto());
                twoCourseCommentvo.setIsShowMoreContent(CommonEnum.UNSHOW_MORE.getCode());
                twoCourseCommentvo.setIsShowMore(CommonEnum.UNSHOW_MORE.getCode());
                twoCourseCommentvo.setReplyId(twoReply.getId());
                twoCourseCommentvo.setShowInput(CommonEnum.UNSHOW_INPUT.getCode());
                twoCourseCommentvo.setReplyName(twoReply.getUsername());
                twoCourseCommentvo.setReplyPhoto(twoReply.getPhoto());

                if (userId != null && !"".equals(userId)) {
                    // 判断当前登录用户是否点赞该评论
                    QueryWrapper<CourseCommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
                    commentLikeQueryWrapper.eq("user_id", userId);
                    commentLikeQueryWrapper.eq("course_comment_id", twoCourseCommentvo.getId());
                    Long selectCount2 = courseCommentLikeMapper.selectCount(commentLikeQueryWrapper);
                    twoCourseCommentvo.setIsLike(selectCount2);

                    // 判断是否为当前登录用户发表的评论
                    if (twoCourseCommentvo.getReplyId().equals(Long.parseLong(userId))) {
                        twoCourseCommentvo.setCommentIsOfLoginUser(CommonEnum.LOGIN_USER_COMMENT.getCode());
                    } else {
                        twoCourseCommentvo.setCommentIsOfLoginUser(CommonEnum.NOT_LOGIN_USER_COMMENT.getCode());
                    }
                }
                    twoList.add(twoCourseCommentvo);


            }
            oneCourseCommentVo.setDownComment(twoList);
            // 若不存在回复评论则加入集合
            if (!existReply) {
                comment_cnt += one_and_two_comment_count;
                courseCommentVoList.add(oneCourseCommentVo);
            }
        }
        selectPage.setRecords(courseCommentVoList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selectPage", selectPage);
        jsonObject.put("commentCount", comment_cnt);
        return R.ok(jsonObject);
    }

    /**
     * 得到时间评论降序/升序课程评论列表(type == 0为默认排序, type == 1为降序，type == 2为升序)
     *
     * @param type        (type == 0为默认排序, type == 1为降序，type == 2为升序)
     * @param userId      当前登录用户id
     * @param courseId    课程id
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 16:45
     */
    @Override
    public R getDownOrUpgradeCourseComment(int type, String userId, long courseId, Long currentPage, Long pageSize) {
        QueryWrapper<CourseComment> courseCommentQueryWrapper = new QueryWrapper<>();
        courseCommentQueryWrapper.eq("course_id", courseId);
        courseCommentQueryWrapper.eq("parent_id", CommonEnum.ONE_LEVEL_COMMENT.getCode());
        if (type == CourseEnum.COURSE_UPSPRT.getCode()) {
            // 课程升序
            courseCommentQueryWrapper.orderByDesc("create_time");
        } else if (type == CourseEnum.COURSE_DOWNSORT.getCode()) {
            // 课程降序
            courseCommentQueryWrapper.orderByAsc("create_time");
        } else if (type == CourseEnum.COURSE_SORT.getCode()) {
            courseCommentQueryWrapper.orderByDesc("like_count");
        }

        Page page = new Page<>(currentPage, pageSize);

        Page selectPage = courseCommentMapper.selectPage(page, courseCommentQueryWrapper);
        List<CourseComment> oneCourseCommentList = selectPage.getRecords();
        // 通过一级评论得到二, 三级评论
        JSONObject jsonObject = getTwoCommentByOneComment(oneCourseCommentList, userId, courseId);
        Long commentCount = jsonObject.getLong("commentCount");
        List<CourseCommentVo> courseCommentVoList = JSONObject.parseArray(jsonObject.getString("courseCommentVoList"), CourseCommentVo.class);
        selectPage.setRecords(courseCommentVoList);

        JSONObject data = new JSONObject();
        data.put("selectPage", selectPage);
        data.put("commentCount", commentCount);
        return R.ok(data);
    }

    /**
     * 判断当前课程用户是否是课程作者
     *
     * @param courseId 课程id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 9:56
     */
    @Override
    public R judgeIsAuthor(Long courseId, String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(false);
        }
        Course course = courseMapper.selectById(courseId);
        Long userIdLong = Long.parseLong(userId);
        if (userIdLong.equals(course.getUserId())) {
            return R.ok(true);
        }
        return R.ok(false);
    }

    /**
     * 精选课程评论
     *
     * @param courseComment 课程评论实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 10:38
     */
    @Override
    public R excellentSelect(CourseComment courseComment) {
        Integer isCuration = courseComment.getIsCuration();
        if (CourseEnum.COURSE_COMMENT_CURATION.getCode().equals(isCuration)) {
            courseComment.setIsCuration(CourseEnum.COURSE_COMMENT_NOT_CURATION.getCode());
        } else {
            courseComment.setIsCuration(CourseEnum.COURSE_COMMENT_CURATION.getCode());
        }

        // 更新精选课程评论
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.updateCourseCurationComment);
        jsonObject.put("courseComment", JSONObject.toJSONString(courseComment));
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                RabbitmqRoutingName.courseUpdateRouting, message);
        return R.ok();
    }

    /**
     * 通过一级评论得到二, 三级评论
     *
     * @param oneCourseCommentList 一级评论集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/8 16:52
     */
    private JSONObject getTwoCommentByOneComment(List<CourseComment> oneCourseCommentList, String userId, long courseId) {
        List<CourseCommentVo> courseCommentVoList = new ArrayList<>();
        // 计算评论总数
        long comment_cnt = 0L;
        for (CourseComment oneCourseComment : oneCourseCommentList) {
            comment_cnt ++ ;
            CourseCommentVo oneCourseCommentVo = new CourseCommentVo();
            BeanUtils.copyProperties(oneCourseComment, oneCourseCommentVo);
            Date commentTime = oneCourseComment.getCreateTime();
            String content = oneCourseComment.getContent();
            Long likeCount = oneCourseComment.getLikeCount();
            Integer isCuration = oneCourseComment.getIsCuration();
            Long senderId = oneCourseComment.getSenderId();
            oneCourseCommentVo.setCommentTime(commentTime);
            oneCourseCommentVo.setContent(content);
            oneCourseCommentVo.setShowInput(CommonEnum.UNSHOW_INPUT.getCode());
            oneCourseCommentVo.setCommentLikeSum(likeCount);
            oneCourseCommentVo.setIsCuration(isCuration);
            oneCourseCommentVo.setSenderId(senderId);
            oneCourseCommentVo.setId(oneCourseComment.getId());
            oneCourseCommentVo.setIsShowMoreContent(CommonEnum.UNSHOW_MORE.getCode());
            oneCourseCommentVo.setIsShowMore(CommonEnum.UNSHOW_MORE.getCode());
            oneCourseCommentVo.setIsCuration(oneCourseComment.getIsCuration());
            // 通过senderId得到发送用户头像姓名
            User user = userMapper.selectById(senderId);
            oneCourseCommentVo.setSenderName(user.getUsername());
            oneCourseCommentVo.setSenderPhoto(user.getPhoto());


            if (userId != null && !"".equals(userId)) {
                // 判断当前登录用户是否点赞该评论
                QueryWrapper<CourseCommentLike> courseCommentLikeQueryWrapper = new QueryWrapper<>();
                courseCommentLikeQueryWrapper.eq("user_id", userId);
                courseCommentLikeQueryWrapper.eq("course_comment_id", oneCourseCommentVo.getId());
                Long selectCount = courseCommentLikeMapper.selectCount(courseCommentLikeQueryWrapper);
                oneCourseCommentVo.setIsLike(selectCount);

                // 判断是否为当前登录用户发表的评论
                if (oneCourseComment.getSenderId().equals(Long.parseLong(userId))) {
                    oneCourseCommentVo.setCommentIsOfLoginUser(CommonEnum.LOGIN_USER_COMMENT.getCode());
                } else {
                    oneCourseCommentVo.setCommentIsOfLoginUser(CommonEnum.NOT_LOGIN_USER_COMMENT.getCode());
                }
            }




            Long courseCommentVoId = oneCourseCommentVo.getId();
            // 通过一级评论id找到二，三级评论信息
            QueryWrapper<CourseComment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("parent_id", courseCommentVoId);
            commentQueryWrapper.eq("course_id", courseId);
            List<CourseComment> twoCourseCommentList = courseCommentMapper.selectList(commentQueryWrapper);

            // 子评论集合
            List<CourseCommentVo> twoList = new ArrayList<>();
            for (CourseComment twoCourseComment : twoCourseCommentList) {
                comment_cnt ++ ;
                CourseCommentVo twoCourseCommentvo = new CourseCommentVo();
                BeanUtils.copyProperties(twoCourseComment, twoCourseCommentvo);
                String twoContent = twoCourseComment.getContent();
                Long twosenderId = twoCourseComment.getSenderId();
                Long twoReplyId = twoCourseComment.getReplyId();
                Date twocCommentTime = twoCourseComment.getCreateTime();
                Long twolikeCount = twoCourseComment.getLikeCount();
                twoCourseCommentvo.setContent(twoContent);
                twoCourseCommentvo.setCommentTime(twocCommentTime);
                twoCourseCommentvo.setCommentLikeSum(twolikeCount);

                // 得到发送者回复者信息
                User twoSender = userMapper.selectById(twosenderId);
                User twoReply = userMapper.selectById(twoReplyId);
                twoCourseCommentvo.setSenderId(twoSender.getId());
                twoCourseCommentvo.setSenderName(twoSender.getUsername());
                twoCourseCommentvo.setSenderPhoto(twoSender.getPhoto());
                twoCourseCommentvo.setIsShowMoreContent(CommonEnum.UNSHOW_MORE.getCode());
                twoCourseCommentvo.setIsShowMore(CommonEnum.UNSHOW_MORE.getCode());
                twoCourseCommentvo.setReplyId(twoReply.getId());
                twoCourseCommentvo.setShowInput(CommonEnum.UNSHOW_INPUT.getCode());
                twoCourseCommentvo.setReplyName(twoReply.getUsername());
                twoCourseCommentvo.setReplyPhoto(twoReply.getPhoto());
                twoList.add(twoCourseCommentvo);

                if (userId != null && !"".equals(userId)) {
                    // 判断当前登录用户是否点赞该评论
                    QueryWrapper<CourseCommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
                    commentLikeQueryWrapper.eq("user_id", userId);
                    commentLikeQueryWrapper.eq("course_comment_id", twoCourseCommentvo.getId());
                    Long selectCount2 = courseCommentLikeMapper.selectCount(commentLikeQueryWrapper);
                    twoCourseCommentvo.setIsLike(selectCount2);

                    // 判断是否为当前登录用户发表的评论
                    if (twoCourseCommentvo.getReplyId().equals(Long.parseLong(userId))) {
                        twoCourseCommentvo.setCommentIsOfLoginUser(CommonEnum.LOGIN_USER_COMMENT.getCode());
                    } else {
                        twoCourseCommentvo.setCommentIsOfLoginUser(CommonEnum.NOT_LOGIN_USER_COMMENT.getCode());
                    }
                }

            }
            oneCourseCommentVo.setDownComment(twoList);
            courseCommentVoList.add(oneCourseCommentVo);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("courseCommentVoList", JSONObject.toJSONString(courseCommentVoList));
        jsonObject.put("commentCount", comment_cnt);
        return jsonObject;
    }
}

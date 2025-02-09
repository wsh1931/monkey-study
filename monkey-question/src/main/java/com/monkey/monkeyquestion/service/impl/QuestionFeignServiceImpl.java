package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyquestion.constant.QuestionPictureEnum;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyCommentMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.mapper.QuestionStatisticsMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.monkeyquestion.pojo.QuestionReplyComment;
import com.monkey.monkeyquestion.pojo.QuestionStatistics;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.rabbitmq.EventConstant;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyquestion.service.QuestionFeignService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/7/31 10:09
 * @version: 1.0
 * @description:
 */
@Service
public class QuestionFeignServiceImpl implements QuestionFeignService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionReplyMapper questionReplyMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private QuestionReplyCommentMapper questionReplyCommentMapper;
    @Resource
    private QuestionStatisticsMapper questionStatisticsMapper;
    /**
     * 通过用户id得到问答列表
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:13
     */
    @Override
    public R getQuestionListByQuestionId(Long userId) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        return R.ok(questionList);
    }

    /**
     * 通过用户id得到用户提问数
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:38
     */
    @Override
    public R getUserQuestionCountByUserId(Long userId) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        Long selectCount = questionMapper.selectCount(questionQueryWrapper);
        return R.ok(selectCount);
    }

    /**
     * 通过用户id得到文章分页提问列表
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 11:39
     */
    @Override
    public R getQuestionListByUserId(Long userId, Long currentPage, Long pageSize) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        questionQueryWrapper.orderByDesc("create_time");
        Page selectPage = questionMapper.selectPage(page, questionQueryWrapper);
        List<Question> questionList = selectPage.getRecords();
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(Math.toIntExact(replyCount));

            // 得到提问收藏数
            questionVo.setUserCollectCount(question.getCollectCount());

            // 得到提问点赞数
            questionVo.setUserLikeCount(question.getLikeCount());

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserPhoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        selectPage.setRecords(questionVoList);
        return R.ok(selectPage);
    }

    /**
     * 问答收藏数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:15
     */
    @Override
    public R addQuestionVCollectSum(Long questionId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.questionCollectCountAddOne);
        jsonObject.put("questionId", questionId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.questionUpdateDirectExchange,
                RabbitmqRoutingName.questionUpdateRouting, message);
        return R.ok(1);
    }

    /**
     * 问答收藏数 - 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:15
     */
    @Override
    public R subQuestionVCollectSum(Long questionId, Date createTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.questionCollectCountSubOne);
        jsonObject.put("questionId", questionId);
        jsonObject.put("createTime", createTime);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.questionUpdateDirectExchange,
                RabbitmqRoutingName.questionUpdateRouting, message);
        return R.ok(1);
    }

    /**
     * 通过问答id得到问答信息
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:31
     */
    @Override
    public R queryQuestionById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", question.getTitle());
        jsonObject.put("picture", QuestionPictureEnum.QUESTION_DEFAULT_PIRCUTR.getUrl());
        jsonObject.put("viewCount", question.getViewCount());
        jsonObject.put("collectCount", question.getCollectCount());
        jsonObject.put("commentCount", question.getReplyCount());
        jsonObject.put("brief", question.getProfile());
        return R.ok(jsonObject);
    }

    /**
     * 通过问答id, 评论id得到问答信息
     *
     * @param questionId 问答id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 15:17
     */
    @Override
    public R queryQuestionAndCommentById(Long questionId, Long commentId) {
        Question question = questionMapper.selectById(questionId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("picture", QuestionPictureEnum.QUESTION_DEFAULT_PIRCUTR.getUrl());
        jsonObject.put("contentTitle", question.getTitle());
        QuestionReplyComment questionReplyComment = questionReplyCommentMapper.selectById(commentId);
        jsonObject.put("title", questionReplyComment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 通过问答id得到问答作者id
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/29 21:27
     */
    @Override
    public Long queryQuestionAuthorById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        return question.getUserId();
    }

    /**
     * 查询问答和回复信息通过id
     *
     * @param questionId 问答id
     * @param questionReplyId 问答回复id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/8 15:18
     */
    @Override
    public R queryQuestionAndReplyById(Long questionId, Long questionReplyId) {
        JSONObject jsonObject = new JSONObject();
        Question question = questionMapper.selectById(questionId);
        jsonObject.put("picture", QuestionPictureEnum.QUESTION_DEFAULT_PIRCUTR.getUrl());
        jsonObject.put("contentTitle", question.getTitle());
        QuestionReply questionReply = questionReplyMapper.selectById(questionReplyId);
        jsonObject.put("title", questionReply.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 得到问答一周发表数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/18 21:17
     */
    @Override
    public R queryQuestionCountRecentlyWeek(String userId) {
        // 一周内所有日期问答
        List<Long> questionCount = new ArrayList<>();
        // 得到一周前数据
        Date before = DateUtils.addDateDays(new Date(), -6);
        Date now = new Date();
        // 得到一周内所有日期
        List<Date> beenTwoDayAllDate = DateSelfUtils.getBeenTwoDayAllDate(before, now);
        beenTwoDayAllDate.forEach(time -> {
            LambdaQueryWrapper<QuestionStatistics> questionStatisticsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            questionStatisticsLambdaQueryWrapper.eq(QuestionStatistics::getUserId, userId);
            questionStatisticsLambdaQueryWrapper.eq(QuestionStatistics::getCreateTime, time);
            Long selectCount = questionStatisticsMapper.selectCount(questionStatisticsLambdaQueryWrapper);
            questionCount.add(selectCount);
        });

        return R.ok(questionCount);
    }
}

package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.mapper.*;
import com.monkey.monkeyquestion.pojo.*;
import com.monkey.monkeyquestion.pojo.vo.QuestionPublishVo;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.rabbitmq.EventConstant;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyquestion.service.QuestionService;
import com.monkey.monkeyquestion.utils.QuestionVoComparator;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionReplyMapper questionReplyMapper;


    @Resource
    private UserMapper userMapper;
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private QuestionLabelMapper questionLabelMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;


    // 得到最新问答列表
    @Override
    public ResultVO getLastQuestionList(Long currentPage, Long pageSize) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.orderByDesc("create_time");
        Page selectPage = questionMapper.selectPage(page, questionQueryWrapper);
        List<Question> questionList = selectPage.getRecords();
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            questionVo.setReplyCount(question.getReplyCount());

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
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    @Override
    public ResultVO publishQuestion(Long userId, String questionForm) {
        QuestionPublishVo questionPublishVo = JSONObject.parseObject(questionForm, QuestionPublishVo.class);
        Question question = new Question();
        BeanUtils.copyProperties(questionPublishVo, question);
        question.setUserId(userId);
        question.setCreateTime(new Date());
        question.setUpdateTime(new Date());
        int insert = questionMapper.insert(question);

        List<Long> labelIdList = questionPublishVo.getLabelId();

        for (Long labelId : labelIdList) {
            QuestionLabel questionLabel = new QuestionLabel();
            questionLabel.setQuestionId(question.getId());
            questionLabel.setLabelId(labelId);
            questionLabel.setCreateTime(new Date());
            questionLabelMapper.insert(questionLabel);
        }

        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 得到最热文章列表
    @Override
    public ResultVO getHottestQuestionList(Long currentPage, Long pageSize) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.orderByDesc("view_count");
        questionQueryWrapper.orderByDesc("like_count");
        questionQueryWrapper.orderByDesc("reply_count");
        questionQueryWrapper.orderByDesc("collect_count");
        Page selectPage = questionMapper.selectPage(page, questionQueryWrapper);
        List<Question> questionList = selectPage.getRecords();
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            questionVo.setReplyCount(question.getReplyCount());

            // 得到提问关注数
            questionVo.setUserCollectCount(question.getCollectCount());

            // 得到提问点赞数
            questionVo.setUserLikeCount(question.getLikeCount());


            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserPhoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        questionVoList.sort(new QuestionVoComparator());
        selectPage.setRecords(questionVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    // 完成等你来答后端查询
    @Override
    public ResultVO getWaitYouQuestionList(Long currentPage, Long pageSize, String userId) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.orderByDesc("create_time");
        Page selectPage = questionMapper.selectPage(page, questionQueryWrapper);
        List<Question> questionList = selectPage.getRecords();
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            questionVo.setReplyCount(question.getReplyCount());
            // 判断该用户是否关注过此问题
            if (userId != null && !userId.equals("")) {
                QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
                questionReplyQueryWrapper.eq("question_id", questionId);
                questionReplyQueryWrapper.eq("user_id", userId);
                Long selectCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
                if (selectCount > 0) {
                    continue;
                }
            }

            // 得到提问点赞数
            questionVo.setUserLikeCount(question.getLikeCount());


            // 得到提问关注数
            questionVo.setUserCollectCount(question.getCollectCount());

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserPhoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        selectPage.setTotal(questionVoList.size());
        selectPage.setRecords(questionVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    // 得到右侧热门回答列表
    @Override
    public ResultVO getRightHottestQuestionList() {
        String redisKey = RedisKeyAndTimeEnum.QUESTION_FIRE_List.getKeyName();
        Integer timeUnit = RedisKeyAndTimeEnum.QUESTION_FIRE_List.getTimeUnit();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, JSONObject.parse(stringRedisTemplate.opsForValue().get(redisKey)));
        }
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.last("limit 10");
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);
            // 得到问答回复数
            questionVo.setReplyCount(question.getReplyCount());

            // 得到提问关注数
            questionVo.setUserCollectCount(question.getCollectCount());

            // 得到提问点赞数
            questionVo.setUserLikeCount(question.getLikeCount());

            questionVoList.add(questionVo);
        }

        questionVoList.sort(new QuestionVoComparator());
        long cnt = 1;
        for (QuestionVo questionVo : questionVoList) {
            questionVo.setSort(cnt ++ );
        }

        if (questionList != null && questionList.size() > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(questionVoList));
            stringRedisTemplate.expire(redisKey, timeUnit, TimeUnit.DAYS);
            return new ResultVO(ResultStatus.OK, null, questionVoList);
        }
        return new ResultVO(ResultStatus.OK, null, "");
    }

    // 用过标签名模糊查询标签列表
    @Override
    public ResultVO getLabelListByLabelName(String labelName) {

        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.like("label_name", "%" + labelName +"%");
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, labelList);
    }

    /**
     * 问答游览数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/30 20:43
     */
    @Override
    public R questionViewCountAddOne(long questionId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.questionViewsAddOne);
        jsonObject.put("questionId", questionId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.questionUpdateDirectExchange,
                RabbitmqRoutingName.questionUpdateRouting, message);
        return R.ok(1);
    }

}

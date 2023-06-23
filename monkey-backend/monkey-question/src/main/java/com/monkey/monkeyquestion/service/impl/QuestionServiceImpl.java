package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.redis.RedisTimeConstant;
import com.monkey.monkeyUtils.redis.RedisUrlConstant;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.mapper.QuestionConcernMapper;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyLabelMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionConcern;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.monkeyquestion.pojo.QuestionReplyLabel;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.service.QuestionService;
import com.monkey.monkeyquestion.utils.QuestionVoComparator;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionReplyMapper questionReplyMapper;

    @Autowired
    private QuestionConcernMapper questionConcernMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private QuestionReplyLabelMapper questionReplyLabelMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    // 得到最新问答列表
    @Override
    public ResultVO getLastQuestionList(Map<String, String>data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
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
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);

            // 得到提问关注数
            QueryWrapper<QuestionConcern> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long concernCount = questionConcernMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setConcernCount(concernCount);

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserphoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        selectPage.setRecords(questionVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    @Override
    public ResultVO publishQuestion(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        Question question = JSONObject.parseObject(data.get("questionForm"), Question.class);
        question.setUserId(userId);
        question.setCreateTime(new Date());
        question.setUpdateTime(new Date());
        int insert = questionMapper.insert(question);
        List<Long> labelIdList = JSON.parseArray(data.get("labelIdList"), Long.class);

        for (Long labelId : labelIdList) {
            QuestionReplyLabel questionReplyLabel = new QuestionReplyLabel();
            questionReplyLabel.setQuestionId(question.getId());
            questionReplyLabel.setLabelId(labelId);
            questionReplyLabelMapper.insert(questionReplyLabel);
        }

        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 得到最热文章列表
    @Override
    public ResultVO getHottestQuestionList(Map<String, String> data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = questionMapper.selectPage(page, null);
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
            questionVo.setReplyCount(replyCount);

            // 得到提问关注数
            QueryWrapper<QuestionConcern> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long concernCount = questionConcernMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setConcernCount(concernCount);

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserphoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        questionVoList.sort(new QuestionVoComparator());
        selectPage.setRecords(questionVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    // 完成等你来答后端查询
    @Override
    public ResultVO getWaitYouQuestionList(Map<String, String> data) {
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        String userId = data.get("userId");
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
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);
            // 判断该用户是否关注过此问题
            if (userId != null && !userId.equals("")) {
                questionReplyQueryWrapper.eq("user_id", userId);
                Long selectCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
                if (selectCount > 0) continue;
            }

            // 得到提问关注数
            QueryWrapper<QuestionConcern> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long concernCount = questionConcernMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setConcernCount(concernCount);

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserphoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        selectPage.setTotal(questionVoList.size());
        selectPage.setRecords(questionVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    // 得到右侧热门回答列表
    @Override
    public ResultVO getRightHottestQuestionList() {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisUrlConstant.FIRE_QUESTION_List))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(RedisUrlConstant.FIRE_QUESTION_List, 0, -1));
        }
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.last("limit 10");
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);

            // 得到提问关注数
            QueryWrapper<QuestionConcern> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long concernCount = questionConcernMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setConcernCount(concernCount);

            questionVoList.add(questionVo);
        }

        questionVoList.sort(new QuestionVoComparator());
        long cnt = 1;
        for (QuestionVo questionVo : questionVoList) {
            questionVo.setSort(cnt ++ );
        }

        redisTemplate.opsForList().rightPushAll(RedisUrlConstant.FIRE_QUESTION_List, questionVoList);
        redisTemplate.expire(RedisUrlConstant.FIRE_QUESTION_List, RedisTimeConstant.FIRE_QUESTION_EXPIRE_TIME, TimeUnit.DAYS);
        return new ResultVO(ResultStatus.OK, null, questionVoList);
    }

    // 用过标签名模糊查询标签列表
    @Override
    public ResultVO getLabelListByLabelName(Map<String, String> data) {
        String labelName = data.get("labelName");
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.like("label_name", "%" + labelName +"%");
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, labelList);
    }
}

package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.monkey.monkeyquestion.service.QuestionService;
import com.monkey.monkeyquestion.utils.QuestionVoComparator;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
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
    private QuestionCollectMapper questionCollectMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private QuestionReplyLabelMapper questionReplyLabelMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private QuestionLikeMapper questionLikeMapper;

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
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);

            // 得到提问收藏数
            QueryWrapper<QuestionCollect> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long collectCount = questionCollectMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setUserCollectCount(collectCount);

            // 得到提问点赞数
            QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
            questionLikeQueryWrapper.eq("question_id", questionId);
            questionVo.setUserLikeCount(questionLikeMapper.selectCount(questionLikeQueryWrapper));

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
    public ResultVO getHottestQuestionList(Long currentPage, Long pageSize) {
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
            QueryWrapper<QuestionCollect> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long collectCount = questionCollectMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setUserCollectCount(collectCount);

            // 得到提问点赞数
            QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
            questionLikeQueryWrapper.eq("question_id", questionId);
            questionVo.setUserLikeCount(questionLikeMapper.selectCount(questionLikeQueryWrapper));


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
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);
            // 判断该用户是否关注过此问题
            if (userId != null && !userId.equals("")) {
                questionReplyQueryWrapper.eq("user_id", userId);
                Long selectCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
                if (selectCount > 0) {
                    continue;
                }
            }

            // 得到提问点赞数
            QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
            questionLikeQueryWrapper.eq("question_id", questionId);
            questionVo.setUserLikeCount(questionLikeMapper.selectCount(questionLikeQueryWrapper));


            // 得到提问关注数
            QueryWrapper<QuestionCollect> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long collectCount = questionCollectMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setUserCollectCount(collectCount);

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
        String redisKey = RedisKeyAndTimeEnum.QUESTION_FIRE_List.getKeyName();
        Integer timeUnit = RedisKeyAndTimeEnum.QUESTION_FIRE_List.getTimeUnit();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(redisKey, 0, -1));
        }
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.last("limit 10");
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到提问点赞数
            QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
            questionLikeQueryWrapper.eq("question_id", questionId);
            questionVo.setUserLikeCount(questionLikeMapper.selectCount(questionLikeQueryWrapper));


            // 得到问答回复数
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);

            // 得到提问关注数
            QueryWrapper<QuestionCollect> questionConcernQueryWrapper = new QueryWrapper<>();
            questionConcernQueryWrapper.eq("question_id", questionId);
            Long collectCount = questionCollectMapper.selectCount(questionConcernQueryWrapper);
            questionVo.setUserCollectCount(collectCount);

            questionVoList.add(questionVo);
        }

        questionVoList.sort(new QuestionVoComparator());
        long cnt = 1;
        for (QuestionVo questionVo : questionVoList) {
            questionVo.setSort(cnt ++ );
        }

        if (questionList != null && questionList.size() > 0) {
            redisTemplate.opsForList().rightPushAll(redisKey, questionVoList);
            redisTemplate.expire(redisKey, timeUnit, TimeUnit.DAYS);
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
        Question question = questionMapper.selectById(questionId);
        question.setVisit(question.getVisit() + 1);
        return R.ok(questionMapper.updateById(question));
    }
}

package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.LabelMapper;

import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.feign.QuestionToUserFeignService;
import com.monkey.monkeyquestion.mapper.*;
import com.monkey.monkeyquestion.pojo.*;
import com.monkey.monkeyquestion.pojo.vo.QuestionReplyCommentVo;
import com.monkey.monkeyquestion.pojo.vo.QuestionReplyVo;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.service.QuestionReplyService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Slf4j
@Service
public class QuestionReplyServiceImpl implements QuestionReplyService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CollectContentConnectMapper collectContentConnectMapper;
    @Autowired
    private QuestionReplyMapper questionReplyMapper;
    @Autowired
    private QuestionReplyLabelMapper questionReplyLabelMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private QuestionReplyCommentMapper questionReplyCommentMapper;
    @Autowired
    private QuestionLikeMapper questionLikeMapper;

    @Autowired
    private QuestionToUserFeignService questionToUserFeignService;

    @Override
    public ResultVO getAuthorVoInfoByQuestionId(long questionId, String fansId) {
        Question question = questionMapper.selectById(questionId);
        Long userId = question.getUserId();
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));
        map.put("nowUserId", fansId);
        ResultVO resultVO = questionToUserFeignService.getUserInformationByUserId(map);
        return resultVO;
    }

    // 通过问答id得到问答信息
    @Override
    public ResultVO getQuestionInfoByQuestionId(long questionId, String userId) {
        QuestionVo questionVo = new QuestionVo();
        Question question = questionMapper.selectById(questionId);
        BeanUtils.copyProperties(question, questionVo);
        // 判断非空
        if (question.getProfile() == null) {
            question.setProfile("");
        }

        // 得到问答用户收藏数
        questionVo.setUserCollectCount(question.getCollectCount());

        // 得到问答回复数
        questionVo.setReplyCount(question.getCommentCount());

        // 得到提问点赞数
        questionVo.setUserLikeCount(question.getLikeCount());

        if (userId != null && !userId.equals("")) {
            QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
            collectContentConnectQueryWrapper.eq("associate_id", questionId);
            collectContentConnectQueryWrapper.eq("type", CommonEnum.COLLECT_QUESTION.getCode());
            // 判断用户是否收藏
            collectContentConnectQueryWrapper.eq("user_id", userId);
            questionVo.setIsCollect(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
            // 判断用户是否点赞
            QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
            questionLikeQueryWrapper.eq("question_id", questionId);
            questionLikeQueryWrapper.eq("user_id", userId);
            questionVo.setIsLike(questionLikeMapper.selectCount(questionLikeQueryWrapper));
        }


        return new ResultVO(ResultStatus.OK, null, questionVo);
    }

    // 通过问答id得到问答标签名
    @Override
    public ResultVO getQuestionLabelNameByQuestionId(long questionId) {
        QueryWrapper<QuestionReplyLabel> questionReplyLabelQueryWrapper = new QueryWrapper<>();
        questionReplyLabelQueryWrapper.eq("question_id", questionId);
        List<QuestionReplyLabel> questionReplyLabelList = questionReplyLabelMapper.selectList(questionReplyLabelQueryWrapper);
        List<Label> labelList = new ArrayList<>();
        for (QuestionReplyLabel questionReplyLabel : questionReplyLabelList) {
            Long labelId = questionReplyLabel.getLabelId();
            Label label = labelMapper.selectById(labelId);
            if (label.getLevel() == 2) {
                labelList.add(label);
            }

        }
        return new ResultVO(ResultStatus.OK, null, labelList);
    }

    // 通过问答id得到问答回复列表
    @Override
    public ResultVO getQuestionReplyListByQuestionId(long questionId, String fansId, Long currentPage, Long pageSize) {
        List<QuestionReplyVo> questionReplyVoList = new ArrayList<>();
        QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
        questionReplyQueryWrapper.eq("question_id", questionId);
        questionReplyQueryWrapper.orderByDesc("update_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = questionReplyMapper.selectPage(page, questionReplyQueryWrapper);
        List<QuestionReply> questionReplyList = selectPage.getRecords();

        for (QuestionReply questionReply : questionReplyList) {
            QuestionReplyVo questionReplyVo = new QuestionReplyVo();
            BeanUtils.copyProperties(questionReply, questionReplyVo);
            // 通过用户id得到用户信息
            Long userId = questionReplyVo.getUserId();
            User user = userMapper.selectById(userId);
            questionReplyVo.setUsername(user.getUsername());
            questionReplyVo.setUserPhoto(user.getPhoto());
            questionReplyVo.setUserBrief(user.getBrief());

            // 判断当前用户是否关注该文章作者
            if (fansId != null && !"".equals(fansId)) {
                R result = questionToUserFeignService.judgeLoginUserAndAuthorConnect(userId, Long.parseLong(fansId));
                if (result.getCode() != R.SUCCESS) {
                    throw new MonkeyBlogException(result.getCode(), result.getMsg());
                }

                Long count = (Long) result.getData(new TypeReference<Long>() {
                });
                questionReplyVo.setIsFans(count);
            } else {
                questionReplyVo.setIsFans(0L);
            }

            // 通过提问回复id得到文章评论数

            questionReplyVo.setArticleCommentCount(questionReply.getQuestionReplyCount());

            // 默认不展示评论
            questionReplyVo.setShowComment(false);
            questionReplyVoList.add(questionReplyVo);
        }

        selectPage.setRecords(questionReplyVoList);
        return new ResultVO(ResultStatus.OK, null, selectPage);
    }

    // 用户问答点赞实现
    @Override
    public ResultVO userLikeQuestion(long questionId, long userId) {
        QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
        questionLikeQueryWrapper.eq("question_id", questionId);
        questionLikeQueryWrapper.eq("user_id", userId);
        QuestionLike questionLike = questionLikeMapper.selectOne(questionLikeQueryWrapper);
        if (questionLike != null) {
            return new ResultVO(ResultStatus.NO, "不可重复点赞", null);
        } else {
            QuestionLike like = new QuestionLike();
            like.setQuestionId(questionId);
            like.setUserId(userId);
            like.setCreateTime(new Date());
            int insert = questionLikeMapper.insert(like);
            if (insert > 0) {
                // 问答点赞数 + 1
                Question question = questionMapper.selectById(questionId);
                question.setLikeCount(question.getLikeCount() + 1);
                questionMapper.updateById(question);
                return new ResultVO(ResultStatus.OK, "点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "发送位置错误，点赞失败", null);
            }
        }
    }

    // 用户问答取消点赞实现
    @Override
    public ResultVO userCancelLikeQuestion(long questionId, long userId) {
        QueryWrapper<QuestionLike> questionLikeQueryWrapper = new QueryWrapper<>();
        questionLikeQueryWrapper.eq("question_id", questionId);
        questionLikeQueryWrapper.eq("user_id", userId);
        QuestionLike questionLike = questionLikeMapper.selectOne(questionLikeQueryWrapper);
        if (questionLike != null) {
            int deleteById = questionLikeMapper.deleteById(questionLike);
            if (deleteById > 0) {
                // 问答点赞数减去 1
                Question question = questionMapper.selectById(questionId);
                question.setLikeCount(question.getLikeCount() - 1);
                questionMapper.updateById(question);
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "发送未知错误，取消点赞失败", null);
            }
        } else {
            return new ResultVO(ResultStatus.NO, "请先进行点赞", null);
        }
    }

    // 通过问答回复id得到文章评论信息
    @Override
    public ResultVO getQuestionCommentByQuestionReplyId(long questionReplyId) {

        // 记录返回结果的数组
        List<QuestionReplyCommentVo> questionReplyCommentVoList = new ArrayList<>();
        // 查询所有的一级评论
        QueryWrapper<QuestionReplyComment> questionReplyCommentOneQueryWrapper = new QueryWrapper<>();
        questionReplyCommentOneQueryWrapper.eq("question_reply_id", questionReplyId);
        questionReplyCommentOneQueryWrapper.eq("parent_id", 0);
        questionReplyCommentOneQueryWrapper.orderByDesc("create_time");
        List<QuestionReplyComment> questionReplyOneCommentList = questionReplyCommentMapper.selectList(questionReplyCommentOneQueryWrapper);

        Long commentSum = 0L;
        for (QuestionReplyComment questionReplyComment : questionReplyOneCommentList) {
            commentSum ++ ;
            QuestionReplyCommentVo questionReplyCommentVo = new QuestionReplyCommentVo();
            BeanUtils.copyProperties(questionReplyComment, questionReplyCommentVo);
            questionReplyCommentVo.setShowInput(false);
            // 得到发布评论人信息
            Long userId = questionReplyCommentVo.getUserId();
            User userComment = userMapper.selectById(userId);
            questionReplyCommentVo.setCommentUserName(userComment.getUsername());
            questionReplyCommentVo.setCommentUserPhoto(userComment.getPhoto());
            // 得到评论回复人信息
            Long replyId = questionReplyCommentVo.getReplyId();
            if (replyId != null) {
                User userCommentReply = userMapper.selectById(replyId);
                questionReplyCommentVo.setReplyUserName(userCommentReply.getUsername());
                questionReplyCommentVo.setReplyUserPhoto(userCommentReply.getPhoto());
            }


            // 通过评论id得到二级，三级评论信息
            Long questionReplyCommentVoId = questionReplyCommentVo.getId();
            QueryWrapper<QuestionReplyComment> questionReplyCommentTwoQueryWrapper = new QueryWrapper<>();
            questionReplyCommentTwoQueryWrapper.eq("parent_id", questionReplyCommentVoId);
            questionReplyCommentTwoQueryWrapper.orderByDesc("create_time");
            List<QuestionReplyComment> questionTowReplyCommentList = questionReplyCommentMapper.selectList(questionReplyCommentTwoQueryWrapper);
            List<QuestionReplyCommentVo> questionReplyCommentVoListTwo = new ArrayList<>();
            for (QuestionReplyComment questionTwoReplyComment: questionTowReplyCommentList) {
                commentSum ++ ;
                QuestionReplyCommentVo questionTwoReplyCommentVo = new QuestionReplyCommentVo();
                BeanUtils.copyProperties(questionTwoReplyComment, questionTwoReplyCommentVo);
                questionTwoReplyCommentVo.setShowInput(false);
                // 得到二三级评论发表用户信息
                Long userIdTwo = questionTwoReplyCommentVo.getUserId();
                if (userIdTwo != null && !userIdTwo.equals("")) {
                    User userCommentTwo = userMapper.selectById(userIdTwo);
                    questionTwoReplyCommentVo.setCommentUserName(userCommentTwo.getUsername());
                    questionTwoReplyCommentVo.setCommentUserPhoto(userCommentTwo.getPhoto());
                }


                // 得到二三级评论回复用户信息
                Long replyIdTwo = questionTwoReplyCommentVo.getReplyId();
                if (replyIdTwo != null) {
                    User userReplyTwo = userMapper.selectById(replyIdTwo);
                    questionTwoReplyCommentVo.setReplyUserName(userReplyTwo.getUsername());
                    questionTwoReplyCommentVo.setReplyUserPhoto(userReplyTwo.getPhoto());
                }

                questionReplyCommentVoListTwo.add(questionTwoReplyCommentVo);
            }
            // 将二级评论列表加入一级评论
            questionReplyCommentVo.setDownComment(questionReplyCommentVoListTwo);
            // 将一级评论加入最终答案
            questionReplyCommentVoList.add(questionReplyCommentVo);

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("questionReplyCommentVoList", questionReplyCommentVoList);
        jsonObject.put("questionCommentCount", commentSum);
        return new ResultVO(ResultStatus.OK, null, jsonObject);
    }
}

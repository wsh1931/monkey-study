package com.monkey.monkeyquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.feign.QuestionToSearchFeignService;
import com.monkey.monkeyquestion.mapper.*;
import com.monkey.monkeyquestion.pojo.*;
import com.monkey.monkeyquestion.service.UserHomeQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author: wusihao
 * @date: 2023/11/28 16:18
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeQuestionServiceImpl implements UserHomeQuestionService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionReplyMapper questionReplyMapper;
    @Resource
    private QuestionLabelMapper questionLabelMapper;
    @Resource
    private QuestionLikeMapper questionLikeMapper;
    @Resource
    private QuestionReplyCommentMapper questionReplyCommentMapper;
    @Resource
    private QuestionToSearchFeignService questionToSearchFeignService;

    /**
     * 通过用户id查询用户发布问答
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/28 16:24
     */
    @Override
    public R queryPublishQuestion(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLambdaQueryWrapper.eq(Question::getUserId, userId);
        questionLambdaQueryWrapper.orderByDesc(Question::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = questionMapper.selectPage(page, questionLambdaQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 通过用户id查询用户回复问答
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/28 16:27
     */
    @Override
    public R queryReplyQuestion(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<QuestionReply> questionReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionReplyLambdaQueryWrapper.eq(QuestionReply::getUserId, userId);
        questionReplyLambdaQueryWrapper.orderByDesc(QuestionReply::getCreateTime);
        questionReplyLambdaQueryWrapper.select(QuestionReply::getQuestionId);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = questionReplyMapper.selectPage(page, questionReplyLambdaQueryWrapper);
        List<QuestionReply> records = selectPage.getRecords();
        List<Question> questionList = new ArrayList<>();
        if (records != null && records.size() > 0) {
            List<Long> questionIdList = records.
                    stream().
                    mapToLong(QuestionReply::getQuestionId)
                    .boxed()
                    .collect(Collectors.toList());
            if (questionIdList.size() > 0) {
                LambdaQueryWrapper<Question> questionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                questionLambdaQueryWrapper.in(Question::getId, questionIdList);
                questionList = questionMapper.selectList(questionLambdaQueryWrapper);
            }
        }

        selectPage.setRecords(questionList);
        return R.ok(selectPage);
    }

    /**
     * 删除问答
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 9:02
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteQuestion(Long questionId) {
        // 删除问答表
        questionMapper.deleteById(questionId);

        // 删除问答标签关联表
        LambdaQueryWrapper<QuestionLabel> questionLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLabelLambdaQueryWrapper.eq(QuestionLabel::getQuestionId, questionId);
        questionLabelMapper.delete(questionLabelLambdaQueryWrapper);

        // 删除问答点赞关系表
        LambdaQueryWrapper<QuestionLike> questionLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLikeLambdaQueryWrapper.eq(QuestionLike::getQuestionId, questionId);
        questionLikeMapper.delete(questionLikeLambdaQueryWrapper);

        // 得到问答回复表id
        LambdaQueryWrapper<QuestionReply> questionReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionReplyLambdaQueryWrapper.eq(QuestionReply::getQuestionId, questionId);
        questionReplyLambdaQueryWrapper.select(QuestionReply::getId);
        List<Object> questionReplyIdList = questionReplyMapper.selectObjs(questionReplyLambdaQueryWrapper);
        if (questionReplyIdList != null && questionReplyIdList.size() > 0) {
            // 删除问答回复表
            questionReplyMapper.deleteBatchIds(questionReplyIdList);

            // 删除提问回复评论表
            LambdaQueryWrapper<QuestionReplyComment> questionReplyCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            questionReplyCommentLambdaQueryWrapper.in(QuestionReplyComment::getQuestionReplyId, questionReplyIdList);
            questionReplyCommentMapper.delete(questionReplyCommentLambdaQueryWrapper);
        }


        // 删除elasticsearch问答表
        questionToSearchFeignService.deleteQuestion(String.valueOf(questionId));
        return R.ok();
    }
}

package com.monkey.monkeyquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.constants.ReportCommentEnum;
import com.monkey.monkeyUtils.constants.ReportContentEnum;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
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
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private MessageCollectMapper messageCollectMapper;
    @Resource
    private ReportContentMapper reportContentMapper;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private ReportCommentMapper reportCommentMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;

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

            // 得到提问回复表id
            LambdaQueryWrapper<QuestionReplyComment> replyCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            replyCommentLambdaQueryWrapper.in(QuestionReplyComment::getQuestionReplyId, questionReplyIdList);
            replyCommentLambdaQueryWrapper.select(QuestionReplyComment::getId);
            List<Object> commentIdList = questionReplyCommentMapper.selectObjs(replyCommentLambdaQueryWrapper);
            if (commentIdList != null && commentIdList.size() > 0) {
                // 删除提问回复评论表
                LambdaQueryWrapper<QuestionReplyComment> questionReplyCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                questionReplyCommentLambdaQueryWrapper.in(QuestionReplyComment::getQuestionReplyId, questionReplyIdList);
                questionReplyCommentMapper.delete(questionReplyCommentLambdaQueryWrapper);

                // 删除消息回复评论表
                LambdaQueryWrapper<MessageCommentReply> messageCommentReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getAssociationId, questionId);
                messageCommentReplyLambdaQueryWrapper.in(MessageCommentReply::getCommentId, commentIdList);
                messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getType, ReportCommentEnum.QUESTION_REPORT.getCode());
                messageCommentReplyMapper.delete(messageCommentReplyLambdaQueryWrapper);

                // 删除举报评论表
                LambdaQueryWrapper<ReportComment> reportCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                reportCommentLambdaQueryWrapper.eq(ReportComment::getType, ReportCommentEnum.QUESTION_REPORT.getCode());
                reportCommentLambdaQueryWrapper.in(ReportComment::getAssociateId, commentIdList);
                reportCommentMapper.delete(reportCommentLambdaQueryWrapper);

                // 删除消息评论点赞表
                LambdaQueryWrapper<MessageLike> messageCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, questionId);
                messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.QUESTION_MESSAGE.getCode());
                messageCommentLikeLambdaQueryWrapper.in(MessageLike::getCommentId, commentIdList);
                messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode());
                messageLikeMapper.delete(messageCommentLikeLambdaQueryWrapper);
            }
        }

        // 删除消息点赞表
        LambdaQueryWrapper<MessageLike> messageLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, questionId);
        messageLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.QUESTION_MESSAGE.getCode());
        messageLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLikeMapper.delete(messageLikeLambdaQueryWrapper);

        // 删除消息收藏表
        LambdaQueryWrapper<MessageCollect> messageCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getAssociationId, questionId);
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getType, MessageEnum.QUESTION_MESSAGE.getCode());
        messageCollectMapper.delete(messageCollectLambdaQueryWrapper);

        // 删除举报内容表
        LambdaQueryWrapper<ReportContent> reportContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportContentLambdaQueryWrapper.eq(ReportContent::getAssociateId, questionId);
        reportContentLambdaQueryWrapper.eq(ReportContent::getType, ReportContentEnum.QUESTION_REPORT.getCode());
        reportContentMapper.delete(reportContentLambdaQueryWrapper);

        // 删除收藏目录关系表
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getType, CommonEnum.COLLECT_QUESTION.getCode())
                .eq(CollectContentConnect::getAssociateId, questionId);
        collectContentConnectMapper.delete(collectContentConnectLambdaQueryWrapper);

        // 删除elasticsearch问答表
        questionToSearchFeignService.deleteQuestion(String.valueOf(questionId));
        return R.ok();
    }
}

package com.monkey.monkeyquestion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.mapper.QuestionReplyCommentMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.monkeyquestion.pojo.QuestionReplyComment;
import com.monkey.monkeyquestion.rabbitmq.EventConstant;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyquestion.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyquestion.service.QuestionReplyCommentService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class QuestionReplyCommentServiceImpl implements QuestionReplyCommentService {

    @Resource
    private QuestionReplyCommentMapper questionReplyCommentMapper;
    @Resource
    private QuestionReplyMapper questionReplyMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    // 发布问答评论
    @Override
    public ResultVO publishQuestionComment(long userId, long questionReplyId, String commentContent) {
        QuestionReplyComment questionReplyComment = new QuestionReplyComment();
        questionReplyComment.setUserId(userId);
        questionReplyComment.setContent(commentContent);
        questionReplyComment.setParentId(0L);
        questionReplyComment.setQuestionReplyId(questionReplyId);
        questionReplyComment.setCreateTime(new Date());
        int insert = questionReplyCommentMapper.insert(questionReplyComment);
        if (insert > 0) {
            // 问答回复数 + 1
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.questionReplyCountAddOne);
            jsonObject.put("questionReplyId", questionReplyId);
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.questionUpdateDirectExchange,
                    RabbitmqRoutingName.questionUpdateRouting, message);
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 问答评论回复功能实现
    @Override
    public ResultVO questionReplyComment(long parentId, long replyId, String questionReplyContent) {
        QuestionReplyComment replyComment = questionReplyCommentMapper.selectById(parentId);
        Long userId = replyComment.getUserId();
        QuestionReplyComment questionReplyComment = new QuestionReplyComment();
        questionReplyComment.setUserId(userId);
        questionReplyComment.setReplyId(replyId);
        questionReplyComment.setContent(questionReplyContent);
        questionReplyComment.setParentId(parentId);
        questionReplyComment.setCreateTime(new Date());
        int insert = questionReplyCommentMapper.insert(questionReplyComment);
        if (insert > 0) {
            // 提问回复评论数 + 1
            Long questionReplyId = replyComment.getQuestionReplyId();
            QuestionReply questionReply = questionReplyMapper.selectById(questionReplyId);
            questionReply.setQuestionReplyCount(questionReply.getQuestionReplyCount() + 1);
            questionReplyMapper.updateById(questionReply);
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }
}

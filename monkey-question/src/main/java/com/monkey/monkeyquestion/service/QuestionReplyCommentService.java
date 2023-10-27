package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface QuestionReplyCommentService {
    // 发布问答评论
    ResultVO publishQuestionComment(long userId, long questionReplyId, String commentContent, Long questionId);

    // 问答评论回复功能实现
    ResultVO questionReplyComment(long parentId, long replyId, String questionReplyContent, Long questionId, Long recipientId);
}

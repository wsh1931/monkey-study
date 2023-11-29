package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeQuestionService {
    // 通过用户id查询用户发布问答
    R queryPublishQuestion(Long userId, Long currentPage, Integer pageSize);

    // 通过用户id查询用户回复问答
    R queryReplyQuestion(Long userId, Long currentPage, Integer pageSize);

    // 删除问答
    R deleteQuestion(Long questionId);
}

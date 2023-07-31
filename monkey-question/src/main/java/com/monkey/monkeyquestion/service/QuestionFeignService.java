package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;

public interface QuestionFeignService {
    // 通过用户id得到问答列表
    R getQuestionListByQuestionId(Long questionId);

    // 通过用户id得到用户提问数
    R getUserQuestionCountByUserId(Long userId);

    // 通过用户id得到文章分页提问列表
    R getQuestionListByUserId(Long userId, Long currentPage, Long pageSize);
}

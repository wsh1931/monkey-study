package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;

public interface QuestionFeignService {
    // 问答游览数 + 1
    R questionViewAddOne(Long questionId);

    // 问答回复数 + 1
    R questionReplyCountAdd(Long questionId);

    // 问答点赞数 + 1
    R questionLikeCountAddOne(Long questionId);

    // 问答点赞数 - 1
    R questionLikeCountSubOne(Long questionId);

    // 问答收藏数 + 1
    R questionCollectCountAddOne(Long questionId);

    // 问答收藏数 - 1
    R questionCollectCountSubOne(Long questionId);

    // 发布问答
    R publishQuestion(ESQuestionIndex esQuestionIndex);

    // 删除问答
    R deleteQuestion(String questionId);
}

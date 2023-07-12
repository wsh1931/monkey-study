package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface QuestionService{
    // 得到最新问答列表
    ResultVO getLastQuestionList(Long currentPage, Long pageSize);

    // 发布问答
    ResultVO publishQuestion(Long userId, String questionForm);

    // 得到最热文章列表
    ResultVO getHottestQuestionList(Long currentPage, Long pageSize);

    // 完成等你来答后端查询
    ResultVO getWaitYouQuestionList(Long currentPage, Long pageSize, String userId);

    // 得到右侧热门回答列表
    ResultVO getRightHottestQuestionList();

    // 用过标签名模糊查询标签列表
    ResultVO getLabelListByLabelName(String labelName);
}

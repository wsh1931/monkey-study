package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface QuestionService{
    // 得到最新问答列表
    ResultVO getLastQuestionList(Map<String, String> data);

    // 发布问答
    ResultVO publishQuestion(Map<String, String> data);

    // 得到最热文章列表
    ResultVO getHottestQuestionList(Map<String, String> data);

    // 完成等你来答后端查询
    ResultVO getWaitYouQuestionList(Map<String, String> data);

    // 得到右侧热门回答列表
    ResultVO getRightHottestQuestionList();

    // 用过标签名模糊查询标签列表
    ResultVO getLabelListByLabelName(Map<String, String> data);
}

package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESQuestionService {
    // 将问答数据库中所有数据存入elasticsearch问答文档中
    R insertQuestionDocument();

    // 查询综合问答列表
    R queryComprehensiveQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询回复最多问答列表
    R queryReplyQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多问答列表
    R queryCollectQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞数最多问答列表
    R queryLikeQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询游览数最多问答列表
    R queryViewQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热问答列表
    R queryHireQuestion(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新问答列表
    R queryLatestQuestion(Integer currentPage, Integer pageSize, String keyword);
}

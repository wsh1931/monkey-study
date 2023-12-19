package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Date;

public interface QuestionFeignService {
    // 通过用户id得到问答列表
    R getQuestionListByQuestionId(Long questionId);

    // 通过用户id得到用户提问数
    R getUserQuestionCountByUserId(Long userId);

    // 通过用户id得到文章分页提问列表
    R getQuestionListByUserId(Long userId, Long currentPage, Long pageSize);

    // 问答收藏数 + 1
    R addQuestionVCollectSum(Long questionId);

    // 问答收藏数 - 1
    R subQuestionVCollectSum(Long questionId, Date createTime);

    // 通过问答id得到问答信息
    R queryQuestionById(Long questionId);

    // 通过问答id, 评论id得到问答信息
    R queryQuestionAndCommentById(Long questionId, Long commentId);

    // 通过问答id得到问答作者id
    Long queryQuestionAuthorById(Long questionId);


    // 查询问答和回复信息通过id
    R queryQuestionAndReplyById(Long questionId, Long questionReplyId);

    // 得到问答一周发表数
    R queryQuestionCountRecentlyWeek(String userId);
}

package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {
    // 查询所有问答
    R queryAllQuestion();

    // 得到所有用户所有问答，点赞，收藏，游览数
    R queryAllUserQuestionInfo();
}

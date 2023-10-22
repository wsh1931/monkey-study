package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface ResourcePayFinishService {
    // 提交资源评分
    R submitResourceScore(Long resourceId, long userId, Integer resourceScore);

    // 查询用户资源评分
    R queryUserResourceScore(long userId, Long resourceId);
}

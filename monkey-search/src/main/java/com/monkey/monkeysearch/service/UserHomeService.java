package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeService {
    // 将用户数据库中所有数据存入elasticsearch用户文档中
    R queryUserAchievement(String userId);
}

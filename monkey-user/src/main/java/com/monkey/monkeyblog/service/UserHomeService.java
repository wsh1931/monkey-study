package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;

public interface UserHomeService {

    // 查询用户信息
    R queryUserAchievement(Long userId);

    // 查询最近访客列表
    R queryLatestVisit(String userId);

    // 添加最近用户访问表
    R addToRecentUserVisit(String userId);
}

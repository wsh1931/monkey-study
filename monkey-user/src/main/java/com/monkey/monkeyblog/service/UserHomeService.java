package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;

public interface UserHomeService {

    // 查询用户信息
    R queryUserAchievement(Long userId);
}

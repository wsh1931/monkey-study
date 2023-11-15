package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;

public interface UserSearchService {
    // 关注用户
    R concernUser(Long concernId, long userId);

    // 取消关注用户
    R cancelConcernUser(Long concernId, long userId);
}

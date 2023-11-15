package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {

    // 得到所有用户粉丝信息
    R queryAllUserFansInfo();

    // 判断当前登录用户与作者是否为粉丝
    R judgeIsFans(String userId, String authorId);
}

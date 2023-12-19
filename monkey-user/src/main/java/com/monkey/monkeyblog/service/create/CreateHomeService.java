package com.monkey.monkeyblog.service.create;

import com.monkey.monkeyUtils.result.R;

public interface CreateHomeService {

    // 查询用户近期收藏信息
    R queryRecentlyCollect();

    // 查询用户近一周原文数
    R queryUserOpusInfoRecentWeek();
}

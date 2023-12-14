package com.monkey.monkeyblog.service.create;

import com.monkey.monkeyUtils.result.R;

public interface CreateHomeService {
    // 得到用户一年中所发表的文章数
    R queryUserOpusCountInYear();

    // 查询用户近期收藏信息
    R queryRecentlyCollect();
}

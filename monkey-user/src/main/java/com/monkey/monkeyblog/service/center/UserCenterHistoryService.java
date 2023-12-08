package com.monkey.monkeyblog.service.center;

import com.monkey.monkeyUtils.result.R;

public interface UserCenterHistoryService {
    // 查询历史内容集合
    R queryHistoryContent(Long currentPage, Integer pageSize);

    // 清除用户历史内容
    R clearHistoryContent();
}

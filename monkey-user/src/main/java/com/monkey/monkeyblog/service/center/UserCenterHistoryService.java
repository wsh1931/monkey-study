package com.monkey.monkeyblog.service.center;

import com.monkey.monkeyUtils.result.R;

public interface UserCenterHistoryService {
    // 查询历史内容集合
    R queryHistoryContent(Long currentPage, Integer pageSize);

    // 清除用户历史内容
    R clearHistoryContent();

    // 查询历史评论集合
    R queryHistoryComment(Long currentPage, Integer pageSize);

    // 清除用户历史评论
    R clearHistoryComment();
}

package com.monkey.monkeyblog.service.message;

import com.monkey.monkeyUtils.result.R;

public interface MessageCollectService {
    // 查询收藏消息集合
    R queryCollectMessage(long userId, Long currentPage, Integer pageSize);

    // 删除收藏消息
    R deleteCollectMessage(Long messageId);

    // 将所有收藏消息置为已读
    R updateAllCollectAlready(String userId);

    // 删除所有收藏已读消息
    R deleteAllCollectMessageOfAlreadyRead(String userId);
}

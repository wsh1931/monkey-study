package com.monkey.monkeyblog.service.message;

import com.monkey.monkeyUtils.result.R;

public interface MessageAttentionService {
    // 查询关注消息集合
    R queryAttentionMessage(long userId, Long currentPage, Integer pageSize);

    // 删除关注消息
    R deleteAttentionMessage(Long messageId);

    // 将所有关注消息置为已读
    R updateAllAttentionAlready(String userId);

    // 删除所有关注已读消息
    R deleteAllAttentionMessageOfAlreadyRead(String userId);
}

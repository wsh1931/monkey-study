package com.monkey.monkeyblog.service.message;

import com.monkey.monkeyUtils.result.R;

public interface MessageLikeService {
    // 查询点赞消息集合
    R queryLikeMessage(long userId, Long currentPage, Integer pageSize);

    // 删除点赞消息
    R deleteLikeMessage(Long messageId);

    // 将所有点赞消息置为已读
    R updateAllLikeAlready(String userId);

    // 删除所有点赞已读消息
    R deleteAllLikeMessageOfAlreadyRead(String userId);
}

package com.monkey.monkeyblog.service.message;

import com.monkey.monkeyUtils.result.R;

public interface CommentReplyService {
    // 查询评论回复消息
    R queryCommentReplyMessage(long userId, Long currentPage, Integer pageSize);

    // 删除评论回复消息
    R deleteCommentReply(Long commentReplyId);

    // 将所有评论回复置为已读
    R updateAllCommentReplyAlready(String userId);

    // 删除所有评论回复消息
    R deleteAllCommentMessageOfAlreadyRead(String userId);
}

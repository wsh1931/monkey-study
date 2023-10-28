package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface ResourceCommentService {
    // 发表评论方法
    R publishCommentMethod(Long resourceId, long userId, String commentContent);

    // 查询评论列表
    R queryCommentList(Long resourceId, String userId, Long currentPage, Integer pageSize);

    // 判断当前登录用户是否是文章作者
    R judgeIsAuthor(Long resourceId, String userId);

    // 得到时间降序评论列表
    R queryTimeDownSortComment(String userId, Long resourceId, Long currentPage, Integer pageSize);

    // 得到时间升序评论列表
    R queryTimeUpgradeComment(String userId, Long resourceId, Long currentPage, Integer pageSize);

    // 查询未回复评论列表
    R queryNotReplyComment(String userId, Long resourceId, Long currentPage, Integer pageSize);

    // 发表评论回复
    R publishCommentReply(Long senderId, Long parentId, long replyId, String replyContent, Long resourceId);

    // 精选评论
    R curationComment(Long commentId);

    // 取消精选评论
    R cancelCurationComment(Long commentId);

    // 置顶评论
    R topComment(Long commentId);

    // 取消置顶评论
    R cancelTopComment(Long commentId);

    // 删除评论
    R deleteComment(Long commentId, Long resourceId);

    // 评论点赞
    R commentLike(long userId, Long commentId, Long recipientId, Long resourceId);

    // 取消评论点赞
    R cancelCommentLike(long userId, Long commentId);
}

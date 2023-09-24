package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityCommentService {
    // 查询默认排序评论列表
    R queryDefaultCommentList(String userId, Long communityArticleId, Long currentPage, Long pageSize);

    // 查询时间升序评论列表
    R queryTimeUpgradeComment(String userId, Long communityArticleId, Long currentPage, Long pageSize);

    // 得到时间降序评论列表
    R queryTimeDownSortComment(String userId, Long communityArticleId, Long currentPage, Long pageSize);

    // 查询未回复评论集合
    R queryNotReplyCommentList(String userId, Long communityArticleId, Long currentPage, Long pageSize);

    // 精选评论
    R curationComment(Long commentId);

    // 取消精选评论
    R cancelCurationComment(Long commentId);

    // 置顶评论
    R topComment(Long commentId);

    // 取消置顶评论
    R cancelTopComment(Long commentId);

    // 删除评论
    R deleteComment(Long commentId, Long communityArticleId);

    // 发表社区文章评论
    R publishComment(long userId, Long communityArticleId, String commentContent);

    // 发表评论回复
    R publishCommentReply(Long senderId, Long parentId, long replyId, String replyContent, Long communityArticleId);

    // 评论点赞
    R commentLike(long userId, Long commentId);

    // 取消评论点赞
    R cancelCommentLike(long userId, Long commentId);
}

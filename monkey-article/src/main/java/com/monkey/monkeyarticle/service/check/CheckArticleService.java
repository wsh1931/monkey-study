package com.monkey.monkeyarticle.service.check;

import com.monkey.monkeyUtils.result.ResultVO;

public interface CheckArticleService {
    // 通过文章id得到文章标签信息
    ResultVO getArticleLabelInfoByArticleId(Long articleId);

    // 通过文章id得到作者信息
    ResultVO getAuthorInfoByArticleId(Long articleId, String fansId);

    // 游览该文章，文章游览数加一
    ResultVO addArticleVisit(Long articleId);

    // 关注作者
    ResultVO likeAuthor(Long userId);

    // 通过文章id查询文章评论信息
    ResultVO getCommentInformationByArticleId(Long articleId, String isLikeUserId);

    // 发布评论
    ResultVO publishComment(Long userId, Long articleId, String content);

    // 评论点赞功能实现
    ResultVO commentLike(Long userId, Long articleId, Long commentId);

    // 评论回复功能实现
    ResultVO replyComment(Long commentId, Long replyId, String replyContent);
}

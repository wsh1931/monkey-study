package com.monkey.monkeyarticle.service.check;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface CheckArticleService {
    // 通过文章id得到文章标签信息
    ResultVO getArticleLabelInfoByArticleId(Map<String, String> data);

    // 通过文章id得到作者信息
    ResultVO getAuthorInfoByArticleId(Map<String, String> data);

    // 游览该文章，文章游览数加一
    ResultVO addArticleVisit(Map<String, String> data);

    // 关注作者
    ResultVO likeAuthor(Map<String, String> data);

    // 通过文章id查询文章评论信息
    ResultVO getCommentInformationByArticleId(Map<String, String> data);

    // 发布评论
    ResultVO publishComment(Map<String, String> data);

    // 评论点赞功能实现
    ResultVO commentLike(Map<String, String> data);

    // 评论回复功能实现
    ResultVO replyComment(Map<String, String> data);
}

package com.monkey.monkeyarticle.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 文章点赞数 + 1
    public static final String articleLikeCountAddOne = "articleLikeCountAddOne";
    // 文章点赞数 - 1
    public static final String articleLikeCountSubOne = "articleLikeCountSubOne";
    // 文章游览数 + 1
    public static final String articleViewCountAddOne = "articleViewCountAddOne";
    // 文章评论数 + 1
    public static final String articleCommentCountAddOne = "articleCommentCountAddOne";
    // 文章评论点赞数 - 1
    public static final String articleCommentLikeCountSubOne = "articleCommentLikeCountSubOne";
    // 文章评论点赞数 + 1
    public static final String articleCommentLikeCountAddOne = "articleCommentLikeCountAddOne";

    // 发布文章
    public static final String publishArticle = "publishArticle";

    // 文章收藏数 - 1
    public static final String articleCollectCountSubOne = "articleCollectCountSubOne";
    // 文章收藏数 + 1
    public static final String articleCollectCountAddOne = "articleCollectCountAddOne";

    // 评论插入文章消息表
    public static final String  commentInsertArticleMessage = "commentInsertArticleMessage";

    // 评论回复插入文章消息表
    public static final String replyInsertArticleMessage = "replyInsertArticleMessage";

    // 加入文章点赞原文消息表
    public static final String insertLikeContentMessage = "insertLikeContentMessage";

    // 加入文章点赞评论消息表
    public static final String insertLikeCommentMessage = "insertLikeCommentMessage";
}

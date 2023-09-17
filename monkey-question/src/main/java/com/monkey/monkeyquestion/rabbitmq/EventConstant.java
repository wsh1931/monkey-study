package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 问答游览数 + 1
    public static final String questionViewsAddOne = "questionViewsAddOne";
    // 问答收藏数 + 1
    public static final String questionCollectCountAddOne = "questionCollectCountAddOne";
    // 问答收藏数 - 1
    public static final String questionCollectCountSubOne = "questionCollectCountSubOne";
    // 问答回复数 + 1(问答回复表)
    public static final String questionReplyCountAddOne = "questionReplyCountAddOne";
    // 问答点赞数 + 1
    public static final String questionLikeCountAddOne = "questionLikeCountAddOne";
    // 问答点赞数 - 1
    public static final String questionLikeCountSubOne = "questionLikeCountSubOne";
    // 问答回复数 + 1(问答表)
    public static final String questionReplyCountAdd = "questionReplyCountAdd";
}

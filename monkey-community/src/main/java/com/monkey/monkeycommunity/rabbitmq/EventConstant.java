package com.monkey.monkeycommunity.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 社区文章数 + 1
    public static final String communityArticleCountAddOne = "communityArticleCountAddOne";

    // 社区文章数 - 1
    public static final String communityArticleCountSubOne = "communityArticleCountSubOne";

    // 插入文章投票表
    public static final String insertArticleVeto = "insertArticleVeto";

    // 插入文章任务表
    public static final String insertArticleTask = "insertArticleTask";

    // 社区成员数 + 1
    public static final String communityMemberCountAddOne = "communityMemberCountAddOne";

    // 社区成员数 - 1
    public static final String getCommunityMemberCountSubOne = "getCommunityMemberCountSubOne";

    // 删除社区文章
    public static final String deleteCommunityArticle = "deleteCommunityArticle";

    // 发布文章
    public static final String publishArticle = "publishArticle";
}

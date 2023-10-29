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
    public static final String insertArticleVote = "insertArticleVote";

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

    // 社区文章评价
    public static final String communityArticleScore = "communityArticleScore";

    // 社区文章投票人数 + 1
    public static final String communityArticleVotePeopleAddOne = "communityArticleVotePeopleAddOne";

    // 社区文章游览数 + 1
    public static final String communityArticleViewCountAddOne = "communityArticleViewCountAddOne";

    // 社区文章任务回复人数 + 1
    public static final String communityArticleTaskReplyCountAddOne = "communityArticleTaskReplyCountAddOne";

    // 删除社区文章任务回复
    public static final String deleteCommunityArticleTaskReply = "deleteCommunityArticleTaskReply";

    // 精选评论
    public static final String curationComment = "curationComment";
    // 取消精选评论
    public static final String  cancelCurationComment = "cancelCurationComment";
    // 置顶评论
    public static final String topComment = "topComment";
    // 取消置顶评论
    public static final String cancelTopComment = "cancelTopComment";
    // 删除评论
    public static final String deleteComment = "deleteComment";

    // 社区文章评论数 + 1
    public static final String communityArticleCommentAddOne = "communityArticleCommentAddOne";

    // 社区文章评论点赞
    public static final String commentLike = "commentLike";

    // 社区文章取消评论点赞
    public static final String cancelCommentLike = "cancelCommentLike";

    // 社区文章点赞
    public static final String communityArticleLike = "communityArticleLike";

    // 社区文章取消点赞
    public static final String communityArticleCancelLike = "communityArticleCancelLike";

    // 社区文章收藏数 + 1
    public static final String communityArticleCollectCountAddOne = "communityArticleCollectCountAddOne";

    // 社区文章收藏数 - 1
    public static final String communityArticleCollectCountSubOne = "communityArticleCollectCountSubOne";

    // 社区文章精选
    public static final String communityArticleExcellent = "communityArticleExcellent";

    // 取消社区文章精选
    public static final String cancelCommunityArticleExcellent = "cancelCommunityArticleExcellent";

    // 社区文章置顶
    public static final String communityArticleTop = "communityArticleTop";
    // 取消社区文章置顶
    public static final String cancelCommunityArticleTop = "cancelCommunityArticleTop";

    // 更新社区文章频道
    public static final String updateCommunityArticleChannel = "updateCommunityArticleChannel";

    // 更新社区排名
    public static final String updateCommunityRank = "updateCommunityRank";

    // 加入社区用户申请表
    public static final String addCommunityUserApplication = "addCommunityUserApplication";

    // 邀请用户进入社区
    public static final String inviteUserEnterCommunity = "inviteUserEnterCommunity";

    // 创建社区
    public static final String createCommunity = "createCommunity";

    // 更新用户关联的角色
    public static final String updateUserConnectRole = "updateUserConnectRole";

    // 增加社区角色
    public static final String addCommunityRole = "addCommunityRole";

    // 删除全部已通过用户申请记录
    public static final String deleteAllSuccessApplicationRecords = "deleteAllSuccessApplicationRecords";

    // 删除全部拒绝用户申请记录
    public static final String deleteAllRefuseApplicationRecords = "deleteAllRefuseApplicationRecords";

    // 更新是否支持前端展示
    public static final String updateSupportShow = "updateSupportShow";

    // 更新是否支持用户发表文章
    public static final String updateSupportUserPublish = "updateSupportUserPublish";

    // 更新是否支持管理员修改
    public static final String updateSupportManageModify = "updateSupportManageModify";

    // 更新社区信息
    public static final String updateCommunityInformation = "updateCommunityInformation";

    // 更新社区通知
    public static final String updateCommunityNotice = "updateCommunityNotice";

    // 评论插入社区文章消息表
    public static final String  commentInsertArticleMessage = "commentInsertArticleMessage";

    // 评论回复插入社区文章消息表
    public static final String replyInsertArticleMessage = "replyInsertArticleMessage";

    // 加入社区文章点赞原文消息表
    public static final String insertCommunityArticleLikeContentMessage = "insertCommunityArticleLikeContentMessage";

    // 加入社区文章点赞评论消息表
    public static final String insertCommunityArticleLikeCommentMessage = "insertCommunityArticleLikeCommentMessage";
}

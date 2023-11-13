package com.monkey.monkeyresource.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 上传资源
    public static final String uploadResource = "uploadResource";

    // redis分类更新集合
    public static final String redisUpdateClassification = "redisUpdateClassification";

    // 更新创作资源用户排行
    public static final String updateCreateResourceUserRank = "updateCreateResourceUserRank";

    // 资源评论数 + 1;
    public static final String resourceCommentCountAddOne = "resourceCommentCountAddOne";

    // 社区文章评论点赞
    public static final String commentLike = "commentLike";

    // 社区文章取消评论点赞
    public static final String cancelCommentLike = "cancelCommentLike";

    // 精选评论
    public static final String curationComment = "curationComment";
    // 取消精选评论
    public static final String  cancelCurationComment = "cancelCurationComment";
    // 置顶评论
    public static final String topComment = "topComment";
    // 取消置顶评论
    public static final String cancelTopComment = "cancelTopComment";

    // 查询订单状态
    public static final String queryOrderStatus = "queryOrderStatus";

    // 插入支付成功更新失败日志
    public static final String insertPayUpdateFailLog = "insertPayUpdateFailLog";

    // 插入支付成功更新成功支付日志
    public static final String insertPayUpdateSuccessLog = "insertPayUpdateSuccessLog";

    // 资源购买人数 + 1
    public static final String resourceBuyCountAddOne = "resourceBuyCountAddOne";

    // 资源购买人数 - 1
    public static final String resourceBuyCountSubOne = "resourceBuyCountSubOne";

    // 资源评分
    public static final String resourceScore = "resourceScore";


    // 插入资源下载表，资源下载人数 + 1
    public static final String insertResourceDown = "insertResourceDown";

    public static final String resourceViewCountAddOne = "resourceViewCountAddOne";

    // 点赞资源
    public static final String resourceLike = "resourceLike";

    // 取消资源点赞
    public static final String cancelResourceLike = "cancelResourceLike";

    // 精选资源
    public static final String curationResource = "curationResource";

    // 取消精选资源
    public static final String cancelCurationResource = "cancelCurationResource";

    // 资源收藏数 + 1
    public static final String resourceCollectCountAddOne = "resourceCollectCountAddOne";

    // 资源收藏数 - 1
    public static final String resourceCollectCountSubOne = "resourceCollectCountSubOne";

    // 评论插入资源消息表
    public static final String  commentInsertResourceMessage = "commentInsertResourceMessage";

    // 评论回复插入资源消息表
    public static final String replyInsertResourceMessage = "replyInsertResourceMessage";

    // 加入资源点赞原文消息表
    public static final String insertResourceLikeContentMessage = "insertResourceLikeContentMessage";

    // 加入资源点赞评论消息表
    public static final String insertResourceLikeCommentMessage = "insertResourceLikeCommentMessage";

    // 删除资源子评论
    public static final String deleteResourceChildrenComment = "deleteResourceChildrenComment";
}

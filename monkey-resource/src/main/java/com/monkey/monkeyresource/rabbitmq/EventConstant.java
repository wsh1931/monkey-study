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

    // 资源评分
    public static final String resourceScore = "resourceScore";
}

package com.monkey.monkeyblog.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 17:16
 * @version: 1.0
 * @description:
 */
public class EventConstant {
    // 删除订单记录
    public static final String deleteOrderRecord = "deleteOrderRecord";
    // 更新订单状态
    public static final String updateOrderStatus = "updateOrderStatus";
    // 插入退款订单
    public static final String insertRefundOrder = "insertRefundOrder";
    // 更新订单信息
    public static final String updateOrderInformation = "updateOrderInformation";
    // 收藏目录表收藏数 + 1;
    public static final String collectContentCountAddOne = "collectContentCountAddOne";
    // 文章收藏数 + 1
    public static final String articleCollectCountAddOne = "articleCollectCountAddOne";
    // 问答收藏数 + 1
    public static final String questionCollectCountAddOne = "questionCollectCountAddOne";
    // 课程收藏数 + 1
    public static final String courseCollectCountAddOne = "courseCollectCountAddOne";
    // 收藏目录表收藏数 - 1;
    public static final String collectContentCountSubOne = "collectContentCountSubOne";
    // 文章收藏数 - 1
    public static final String articleCollectCountSubOne = "articleCollectCountSubOne";
    // 问答收藏数 - 1
    public static final String questionCollectCountSubOne = "questionCollectCountSubOne";
    // 课程收藏数 - 1
    public static final String courseCollectCountSubOne = "courseCollectCountSubOne";

    // 将用户访问其他用户主页信息加入最近游览表中
    public static final String insertUserRecentlyView = "insertUserRecentlyView";

    // 社区文章收藏数 + 1
    public static final String communityArticleCollectAddOne = "communityArticleCollectAddOne";

    // 社区文章收藏数 - 1
    public static final String communityArticleCollectSubOne = "communityArticleCollectSubOne";

    // 资源收藏数 + 1
    public static final String resourceCollectCountAddOne = "resourceCollectCountAddOne";

    // 资源收藏数 - 1
    public static final String resourceCollectCountSubOne = "resourceCollectCountSubOne";

    // 更新已经阅读的评论回复消息
    public static final String updateCommentReplyMessageAlready = "updateCommentReplyMessageAlready";

    // 更新已经阅读的点赞消息
    public static final String updateLikeMessageAlready = "updateLikeMessageAlready";

    // 插入收藏消息表
    public static final String insertCollectMessage = "insertCollectMessage";

    // 更新收藏消息列表为已读
    public static final String updateCollectMessageAlready = "updateCollectMessageAlready";

    // 插入关注消息表
    public static final String insertConcernMessage = "insertConcernMessage";

    // 更新用户关注消息为已读，通过消息id集合
    public static final String updateAttentionMessageAlready = "updateAttentionMessageAlready";

    // 插入支付成功更新失败日志
    public static final String insertPayUpdateFailLog = "insertPayUpdateFailLog";

    // 插入支付成功更新成功支付日志
    public static final String insertPayUpdateSuccessLog = "insertPayUpdateSuccessLog";

    // 插入举报内容表
    public static final String insertReportContent = "insertReportContent";

    // 插入举报评论表
    public static final String insertReportComment = "insertReportComment";

    // 用户粉丝数 + 1
    public static final String userFansCountAddOne = "userFansCountAddOne";

    // 用户粉丝数 - 1
    public static final String userFansCountSubOne = "userFansCountSubOne";

    // 插入邮箱验证码发送
    public static final String insertEmailCode = "insertEmailCode";
}

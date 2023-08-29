package com.monkey.monkeyUtils.constants;

/**
 * @author: wusihao
 * @date: 2023/8/8 10:01
 * @version: 1.0
 * @description: 通用常量类
 */
public class CommonConstant {
    public static final String likeSuccess = "点赞成功";

    public static final String cancelLikeSuccess = "取消点赞成功";

    public static final String publishCourseCommentSuccess = "发表课程评论成功";

    public static final String publishCourseCommentFail = "发表课程评论失败";

    public static final String publishCourseCommentReplySuccess = "发表课程评论回复成功";

    public static final String publishCourseCommentReplyFail = "发表课程评论回复失败";

    // 课程弹幕撤销时间，单位秒
    public static final Integer courseBarrageCancelTime = 120;

    // 创建交易尺幅失败
    public static final String createTradePayFail = "创建交易支付失败";

    // 支付宝交易成功返回状态码
    public static final String apliPayTradeSuccess = "TRADE_SUCCESS";

    public static final String cancelOrderFail = "用户取消订单失败";

    public static final String queryOrder = "主动查询阿里云订单失败";

    public static final String orderNotExist = "订单不存在";

    public static final String refundException = "退款异常";
}

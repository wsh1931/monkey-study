package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.result.R;

public interface OrderCenterService {

    // 得到订单类型的数量（全部，已付款，未付款，待评价）
    R getOrderTypeNumber(long userId);

    // 得到全部订单列表
    R getAllOrderList(long userId, int currentPage, int pageSize);

    // 得到已完成订单列表
    R getAlreadyFinishedOrderList(Long userId, int currentPage, int pageSize);

    // 得到待评价订单列表
    R getWaitEvaluateOrderList(Long userId, int currentPage, int pageSize);

    // 得到待付款订单列表
    R getWaitPayOrderList(Long userId, int currentPage, int pageSize);

    // 删除订单记录
    R deleteOrderRecord(long orderInformationId);

    // 用户取消订单实现
    R cancelOrder(long orderInformationId);

    // 得到用户已取消订单列表
    R getUserCanceledOrderList(Long userId, int currentPage, int pageSize);

    // 得到超时已关闭订单列表
    R getExceedTimeAlreadyCloseOrderList(Long userId, int currentPage, int pageSize);

    // 申请阿里云支付退款实现
    R orderRefund(OrderInformation orderInformation, String reason);

    // 得到退款成功订单列表
    R getRefundSuccessOrderList(Long userId, int currentPage, int pageSize);

    // 得到退款失败订单列表
    R getRefundFailOrderList(Long userId, int currentPage, int pageSize);

    // 退款查询接口
    R refundQuery(Long orderInformationId);

    // 通过账单类型和日期获取账单Url
    R queryTradeBill(String billDate, String billType);
}

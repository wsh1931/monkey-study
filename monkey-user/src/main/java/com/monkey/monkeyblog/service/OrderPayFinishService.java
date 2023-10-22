package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;

public interface OrderPayFinishService {
    // 通过订单id得到订单信息
    R queryOrderInfoByOrderId(long orderId);
}

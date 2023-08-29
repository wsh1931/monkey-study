package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

public interface CoursePayFinishService {
    // 通过订单id得到订单信息
    R queryOrderInfoByOrderId(long orderId);
}

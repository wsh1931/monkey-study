package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Map;

public interface CoursePayService {
    // 通过课程id得到课程信息
    R getCourseInfoByCourseId(long courseId, long userId);

    // 统一下单并支付页面接口
    R tradePagePay(long courseId, Integer isSelectChargeWay);

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    String finishPayNotice(Map<String, String> data);

    // 主动查询阿里云订单
    String queryOrder(Long orderInformationId);

//    调用支付宝提供的统一交易关闭接口
    void closeOrder(long orderInformationId);
}

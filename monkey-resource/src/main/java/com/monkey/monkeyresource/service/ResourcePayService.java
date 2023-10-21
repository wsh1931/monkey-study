package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Map;


public interface ResourcePayService {
    // 得到资源基本信息
    R queryResourceInfo(long userId, Long resourceId);

    // 提交资源订单
    R submitResourceOrder(long userId, Long resourceId, Integer payWay);

    // 主动查询阿里支付订单信息
    String queryAliPayOrder(Long orderInformationId);

    // 支付宝关单接口
    void closeAliPayOrder(Long orderInformationId);

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    String finishPayNotice(Map<String, String> data);
}

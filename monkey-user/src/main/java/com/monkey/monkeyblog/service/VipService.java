package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Map;

public interface VipService {
    // 查询会员价格列表
    R queryVipPrice();

    // 查询会员专属特权列表
    R queryVipPrivilegeList();

    // 提交vip订单
    R submitVipOrder(Long userId, Integer payWay, Integer monkey, Integer selectVipPriceId);

    // 判断用户是否为会员
    R judgeIsVip(String userId);

    // 主动查询阿里支付订单信息
    String queryAliPayOrder(Long orderInformationId);

    // 支付宝关单接口
    void closeAliPayOrder(Long orderInformationId);

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    String finishPayNotice(Map<String, String> data);
}

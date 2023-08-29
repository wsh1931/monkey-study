package com.monkey.monkeyUtils.constants;

/**
 * @author: wusihao
 * @date: 2023/8/28 16:58
 * @version: 1.0
 * @description: 阿里云支付的trade_status常量
 */
public enum AliPayTradeStatusEnum {
    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),
    TRADE_CLOSED("TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),
    TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款"),
    ;



    private String status;
    private String description;

    AliPayTradeStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}

package com.monkey.monkeyUtils.exception;

/**
 * @author: wusihao
 * @date: 2023/7/20 16:41
 * @version: 1.0
 * @description: 异常枚举类
 */
public enum ExceptionEnum {
    NOT_ENUM(-1, "未找到该异常"),
    USER_NOT_EXIST(1000, "用户不存在"),

    Delete_USERFANS_FAIL(1100, "删除userFans失败"),
    ADD_USERFANS_FAIL(1101, "插入userFans失败"),
    PERMISSION_DENIFY(1200, "认证失败，无法访问系统资源"),

    WEBSOCKET_SEND_MESSAGE(1300, "WebSocket发送信息异常"),

    COURSE_BARRAGE_EXPIRE(1400, "弹幕发送时间已超过2分钟，无法撤回"),

    // 订单异常
    ORDER_NOT_EXIST(1500, "订单不存在"),
    MONKEY_VERITY_FAIL(1501, "总支付金额与实际金额不符合"),
    MERCHANT_PID_VERIFY_FAIL(1502, "商家pid校验失败"),
    APP_ID_VERIFY_FAIL(1503, "appId校验失败"),
    PAY_FAIL(1504, "支付失败")


        ;

    private Integer code;
    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    //获取指定值枚举类
    public static ExceptionEnum getCommonEnum(Integer code) {
        if (null == code) {//code为null
            return ExceptionEnum.NOT_ENUM;
        }
        ExceptionEnum[] values = ExceptionEnum.values();
        for (ExceptionEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return ExceptionEnum.NOT_ENUM;//没找到、
    }
}

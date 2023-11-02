package com.monkey.monkeyblog.constant;

/**
 * @author: wusihao
 * @date: 2023/10/13 8:13
 * @version: 1.0
 * @description:
 */
public enum UserEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    REFUND_EXPIRE_TIME(3, "退款过期时间(3天)"),

    ONE_REPORT_TYPE(1, "一级举报类型"),
    TWO_REPORT_TYPE(2, "二级举报类型")

    ;
    private Integer code;
    private String msg;

    UserEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

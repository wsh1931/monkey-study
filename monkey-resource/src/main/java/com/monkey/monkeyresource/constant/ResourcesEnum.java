package com.monkey.monkeyresource.constant;

/**
 * @author: wusihao
 * @date: 2023/10/13 8:13
 * @version: 1.0
 * @description:
 */
public enum ResourcesEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    // 资源是否精选
    NOT_CURATION(0, "未被精选"),
    IS_CURATION(1, "已精选"),

    // 资源是否打折
    IS_DISCOUNT(1, "已打折"),
    NOT_DISCOUNT(0, "未打折"),

    // 资源是否通过审核
    SUCCESS(1, "已通过审核"),
    FAIL(-1, "未通过审核"),
    REVIEWING(0, "审核中"),
    ;
    private Integer code;
    private String msg;

    ResourcesEnum(Integer code, String msg) {
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

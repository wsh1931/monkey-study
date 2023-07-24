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
    ADD_USERFANS_FAIL(1101, "插入userFans失败")


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

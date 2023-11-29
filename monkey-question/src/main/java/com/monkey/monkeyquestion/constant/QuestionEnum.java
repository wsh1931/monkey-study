package com.monkey.monkeyquestion.constant;

/**
 * @author: wusihao
 * @date: 2023/11/28 16:46
 * @version: 1.0
 * @description:
 */
public enum QuestionEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),


    // 鼠标是否悬浮在显示更多图标上
    IS_HOVER(1, "悬浮"),
    NOT_HOVER(0, "未悬浮"),
    ;
    private Integer code;
    private String msg;

    QuestionEnum(Integer code, String msg) {
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
    public static QuestionEnum getCourseEnum(Integer code) {
        //code为null
        if (null == code) {
            return QuestionEnum.NOT_ENUM;
        }
        QuestionEnum[] values = QuestionEnum.values();
        for (QuestionEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return QuestionEnum.NOT_ENUM;
    }
}

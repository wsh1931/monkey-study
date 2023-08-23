package com.monkey.monkeycourse.constant;

/**
 * @author: wusihao
 * @date: 2023/7/27 16:52
 * @version: 1.0
 * @description:
 */
public enum FormTypeEnum {
    // 未定义该枚举类
    NOT_ENUM(-1L, "未找到指定枚举类"),
    FORM_TYPE_ALL(-1L, "全部"),
    FORM_TYPE_FREE(1L, "免费课"),
    FORM_TYPE_VIP(2L, "会员课"),
    FORM_TYPE_TOLL(3L, "收费课"),
    FORM_TYPE_COMMEND(4L, "官方推荐")
            ;

    private Long code;
    private String msg;

    FormTypeEnum(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    //获取指定值枚举类
    public static FormTypeEnum getFormTypeEnum(Long code) {
        if (null == code) {//code为null
            return FormTypeEnum.NOT_ENUM;
        }
        FormTypeEnum[] values = FormTypeEnum.values();
        for (FormTypeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return FormTypeEnum.NOT_ENUM;//没找到、
    }
}

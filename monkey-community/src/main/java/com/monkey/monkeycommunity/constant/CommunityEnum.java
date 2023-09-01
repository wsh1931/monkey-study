package com.monkey.monkeycommunity.constant;

public enum CommunityEnum {

    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    ONE_LEVEL_LABEL(1, "一级标签"),
    TWO_LEVEL_LABEL(2, "二级标签"),

    ;
    private Integer code;
    private String msg;

    CommunityEnum(Integer code, String msg) {
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
    public static CommunityEnum getCourseEnum(Integer code) {
        if (null == code) {//code为null
            return CommunityEnum.NOT_ENUM;
        }
        CommunityEnum[] values = CommunityEnum.values();
        for (CommunityEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CommunityEnum.NOT_ENUM;//没找到、
    }
}

package com.monkey.monkeyUtils.constants;

// 社区权限枚举类
public enum CommunityMenuEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    STATUS_NORMAL(0, "正常"),
    STATUS_INHABIT(1, "停用")
    ;

    private Integer code;
    private String msg;

    CommunityMenuEnum(Integer code, String msg) {
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
    public static CommunityMenuEnum getCommunityMenuEnum(Integer code) {
        //code为null
        if (null == code) {
            return CommunityMenuEnum.NOT_ENUM;
        }
        CommunityMenuEnum[] values = CommunityMenuEnum.values();
        for (CommunityMenuEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return CommunityMenuEnum.NOT_ENUM;
    }
}

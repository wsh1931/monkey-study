package com.monkey.monkeycommunity.constant;

public enum CommunityAttributeEnum {

    // 未定义该枚举类
    NOT_ENUM(-1L, "未找到指定枚举类"),
    PRIVATE_COMMUNITY(1L, "个人社区"),
    ENTERPRISE_COMMUNITY(2L, "企业社区"),
    COLLEGE_COMMUNITY(3L, "企业社区"),
    ELSE(4L, "其他"),

    ;
    private Long code;
    private String msg;

    CommunityAttributeEnum(Long code, String msg) {
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
    public static CommunityAttributeEnum getCourseEnum(Integer code) {
        if (null == code) {//code为null
            return CommunityAttributeEnum.NOT_ENUM;
        }
        CommunityAttributeEnum[] values = CommunityAttributeEnum.values();
        for (CommunityAttributeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CommunityAttributeEnum.NOT_ENUM;//没找到、
    }
}

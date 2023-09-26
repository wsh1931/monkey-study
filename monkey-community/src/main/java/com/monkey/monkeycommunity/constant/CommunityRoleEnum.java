package com.monkey.monkeycommunity.constant;

/**
 * @author: wusihao
 * @date: 2023/9/3 10:40
 * @version: 1.0
 * @description:
 */
public enum CommunityRoleEnum {
    // 未定义该枚举类
    NOT_ENUM(-1L, "未找到指定枚举类"),

    PRIMARY_ADMINISTRATOR(1L, "主管理员"),
    ADMINISTRATOR(2L, "管理员"),
    MEMBER(3L, "社区成员"),
    ;
    private Long code;
    private String msg;

    CommunityRoleEnum(Long code, String msg) {
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
    public static CommunityRoleEnum getCommunityRoleEnum(Long code) {
        if (null == code) {//code为null
            return CommunityRoleEnum.NOT_ENUM;
        }
        CommunityRoleEnum[] values = CommunityRoleEnum.values();
        for (CommunityRoleEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CommunityRoleEnum.NOT_ENUM;//没找到、
    }
}

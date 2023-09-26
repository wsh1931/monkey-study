package com.monkey.monkeycommunity.constant;

/**
 * @author: wusihao
 * @date: 2023/9/26 9:59
 * @version: 1.0
 * @description: 社区官方枚举类
 */
public enum CommunityOfficeEnum {
    // 未定义该枚举类
    COMMUNITY_OFFICE_ID(-1L, "官方社区id"),
            ;
    private Long code;
    private String msg;

    CommunityOfficeEnum(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

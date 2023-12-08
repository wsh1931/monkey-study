package com.monkey.monkeyUtils.constants;

import io.swagger.models.auth.In;

/**
 * @author: wusihao
 * @date: 2023/12/7 8:57
 * @version: 1.0
 * @description: 历史游览枚举类
 */
public enum HistoryViewEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到此枚举类"),
    ARTICLE(0, "文章"),
    QUESTION(1, "问答"),
    COURSE(2, "课程"),
    COMMUNITY_ARTICLE(3, "社区文章"),
    RESOURCE(4, "资源")
            ;

    private Integer code;
    private String msg;

    HistoryViewEnum(Integer code, String msg) {
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
    public static HistoryViewEnum getHistoryViewEnum(Integer code) {
        //code为null
        if (null == code) {
            return HistoryViewEnum.NOT_ENUM;
        }
        HistoryViewEnum[] values = HistoryViewEnum.values();
        for (HistoryViewEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return HistoryViewEnum.NOT_ENUM;
    }
}

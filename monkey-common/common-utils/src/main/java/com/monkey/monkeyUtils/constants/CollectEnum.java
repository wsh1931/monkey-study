package com.monkey.monkeyUtils.constants;

public enum CollectEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    COLLECT_ARTICLE(0, "文章收藏"),

    COLLECT_QUESTION(1, "问答收藏"),

    COLLECT_COURSE(2, "课程收藏"),

    COLLECT_COMMUNITY_ARTICLE(3, "社区文章收藏"),

    COLLECT_RESOURCE(4, "资源收藏"),
    ;

    private Integer code;
    private String msg;

    CollectEnum(Integer code, String msg) {
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
    public static CollectEnum getCollectEnum(Integer code) {
        if (null == code) {//code为null
            return CollectEnum.NOT_ENUM;
        }
        CollectEnum[] values = CollectEnum.values();
        for (CollectEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CollectEnum.NOT_ENUM;//没找到、
    }
}

package com.monkey.monkeyUtils.constants;

public enum CollectEnum {
    // 未定义该枚举类
    ALL(-1, "全部"),

    COLLECT_ARTICLE(0, "文章"),
    COLLECT_QUESTION(1, "问答"),

    COLLECT_COURSE(2, "课程"),

    COLLECT_COMMUNITY_ARTICLE(3, "社区文章"),

    COLLECT_RESOURCE(4, "资源"),
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
        //code为null
        if (null == code) {
            return CollectEnum.ALL;
        }
        CollectEnum[] values = CollectEnum.values();
        for (CollectEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return CollectEnum.ALL;
    }
}

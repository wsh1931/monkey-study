package com.monkey.monkeyUtils.constants;

public enum CommonEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    // 一级标签
    LABEL_LEVEL_ONE(1, "一级标签"),
    // 二级标签
    LABEL_LEVEL_TWO(2, "二级标签"),
    COURSE_FREE(0, "课程免费"),
    COURSE_UNFREE(1, "课程收费"),
    COLLECT_ARTICLE(0, "文章收藏"),
    COLLECT_QUESTION(1, "问答收藏"),
    COLLECT_COURSE(2, "课程收藏")
    ;

    private Integer code;
    private String msg;

    CommonEnum(Integer code, String msg) {
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
    public static CommonEnum getCommonEnum(Integer code) {
        if (null == code) {//code为null
            return CommonEnum.NOT_ENUM;
        }
        CommonEnum[] values = CommonEnum.values();
        for (CommonEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CommonEnum.NOT_ENUM;//没找到、
    }
}

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
    COLLECT_COURSE(2, "课程收藏"),
    UNCOLLECT(0, "未收藏"),
    COLLECT(1, "已收藏"),
    COURSE_UNDISCOUNT(0, "课程未打折"),
    COURSE_DISCOUNT(1, "课程已打折"),
    UNAUTHORIZED_LOGIN(3, "未授权登录返回前端数目"),

    ONE_LEVEL_COMMENT(0, "一级评论"),

    SHOW_INPUT(1, "展示输入框"),
    UNSHOW_INPUT(0, "不展示输入框"),

    SHOW_MORE(1, "展示更多"),
    UNSHOW_MORE(0, "不展示更多"),
    NOT_LOGIN_USER_COMMENT(0, "不是当前登录用户发表的评论"),
    LOGIN_USER_COMMENT(1, "是当前登录用户发表的评论"),

    COURSE_SORT(0, "代表默认"),
    COURSE_DOWNSORT(1, "查找课程降序评论列表"),
    COURSE_UPSPRT(2, "课程升序评论列表")
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

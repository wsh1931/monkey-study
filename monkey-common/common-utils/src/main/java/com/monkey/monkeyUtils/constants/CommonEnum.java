package com.monkey.monkeyUtils.constants;

public enum CommonEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    // 一级标签
    LABEL_LEVEL_ONE(1, "一级标签"),
    // 二级标签
    LABEL_LEVEL_TWO(2, "二级标签"),

    COLLECT_ARTICLE(0, "文章收藏"),
    COLLECT_QUESTION(1, "问答收藏"),
    COLLECT_COURSE(2, "课程收藏"),
    UNCOLLECT(0, "未收藏"),
    COLLECT(1, "已收藏"),

    UNAUTHORIZED_LOGIN(3, "未授权登录返回前端数目"),

    ONE_LEVEL_COMMENT(0, "一级评论"),

    SHOW_INPUT(1, "展示输入框"),
    UNSHOW_INPUT(0, "不展示输入框"),

    SHOW_MORE(1, "展示更多"),
    UNSHOW_MORE(0, "不展示更多"),
    NOT_LOGIN_USER_COMMENT(0, "不是当前登录用户发表的评论"),
    LOGIN_USER_COMMENT(1, "是当前登录用户发表的评论"),

    // 该登录用户是否为会员
    NOT_VIP(0, "非会员"),
    IS_VIP(1, "会员"),

    IS_AUTHORITY(1, "有权限"),
    NOT_AUTHORITY(0, "权限不足"),

    NOT_SELECTED(0, "未被选中"),
    IS_SELECTED(1, "被选中"),

    IS_FANS(1, "粉丝"),
    NOT_FANS(0, "不是粉丝"),

    ALREADY_REGISTER_EMAIL(1, "已注册邮箱"),
    NOT_REGISTER_EMAIL(0, "未注册邮箱")
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

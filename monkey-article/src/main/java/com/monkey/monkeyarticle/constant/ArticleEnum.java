package com.monkey.monkeyarticle.constant;

public enum ArticleEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    IS_LIKE_ARTICLE(1, "已点赞文章"),
    NOT_LIKE_ARTICLE(0, "未点赞文章"),

    NOT_COLLECT_ARTICLE(0, "未收藏文章"),
    ALREADY_COLLECT_ARTICLE(1, "已收藏文章"),
    // 鼠标是否悬浮在显示更多图标上
    IS_HOVER(1, "悬浮"),
    NOT_HOVER(0, "未悬浮"),

    SUCCESS(1, "已通过审核"),
    FAIL(-1, "未通过审核"),
    REVIEWING(0, "审核中"),
    ;

    private Integer code;
    private String msg;

    ArticleEnum(Integer code, String msg) {
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
    public static ArticleEnum getArticleEnum(Integer code) {
        if (null == code) {//code为null
            return ArticleEnum.NOT_ENUM;
        }
        ArticleEnum[] values = ArticleEnum.values();
        for (ArticleEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return ArticleEnum.NOT_ENUM;//没找到、
    }
}

package com.monkey.monkeyarticle.constant;

public enum ArticleEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    IS_LIKE_ARTICLE(1, "已点赞文章"),
    NOT_LIKE_ARTICLE(0, "未点赞文章")
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

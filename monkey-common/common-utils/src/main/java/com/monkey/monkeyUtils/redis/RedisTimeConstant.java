package com.monkey.monkeyUtils.redis;

public class RedisTimeConstant {
    // 标签过期时间(单位分钟)
    public static final Integer LABEL_EXPIRE_TIME = 10;

    // 用户主页vo过期时间(单位小时)
    public static final Integer USERVO_EXPIRE_TIME = 1;

    // 文章vo过期时间（单位小时）
    public static final Integer ARTICLE_EXPIRE_TIME = 1;

    // 文章评论过期时间（单位分钟）
    public static final Integer ARTICLE_COMMENT_EXPIRE_TIME = 10;

    // 单位（天）
    public static final Integer FIRE_RECENTLY_EXPIRE_TIME = 1;

    // 热门回答列表（单位天）
    public static final Integer FIRE_QUESTION_EXPIRE_TIME = 1;

    // 注册验证码过期时间（单位分钟）
    public static final Integer REGISTER_VERFY_CODE_EXPIRE_TIME = 1;
}

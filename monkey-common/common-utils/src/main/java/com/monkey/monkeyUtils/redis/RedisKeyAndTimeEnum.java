package com.monkey.monkeyUtils.redis;

public enum RedisKeyAndTimeEnum {
    NOT_ENUM(-1, "不存在该字段"),
    // 标签过期时间(单位/分钟)
    ARTICLE_LABEL_LIST(1, "articleLabelList"),
    // 查看最近最火文章（单位/天）
    RECENT_FIRE_ARTICLE(1, "recentFireArticle"),
    // 查看最近最火问答列表(单位/天)
    QUESTION_FIRE_List(1, "fireQuestionList"),
    // 注册验证码(单位/天)
    VERFY_CODE_REGISTER(1, "registerVerfyCode"),

    // 课程一级标签列表(单位/时)
    COURSE_ONE_LABEL_List(1, "courseOneLabel"),

    // 课程二级标签列表(单位/时)
    COURSE_TWO_LABEL_List(1, "courseTwoLabel"),

//    单位天 String redisKey = "courseDirectory: " + "courseId = " + courseId + " userId = " + userId;
    COURSE_DIRECTORY(1, "courseDirectory")
    ;

    private Integer timeUnit;
    private String keyName;

    RedisKeyAndTimeEnum(Integer timeUnit, String keyName) {
        this.timeUnit = timeUnit;
        this.keyName = keyName;
    }

    public Integer getTimeUnit() {
        return timeUnit;
    }

    public String getKeyName() {
        return keyName;
    }
}

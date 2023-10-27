package com.monkey.monkeyUtils.constants;

public enum MessageEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    // 消息类型
    ARTICLE_MESSAGE(0, "文章消息"),
    QUESTION_MESSAGE(1, "问答消息"),
    COURSE_MESSAGE(2, "课程消息"),
    COMMUNITY_ARTICLE_MESSAGE(3, "社区文章消息"),
    RESOURCE_MESSAGE(4, "资源消息")
    ;

    private Integer code;
    private String msg;

    MessageEnum(Integer code, String msg) {
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
    public static MessageEnum getMessageEnum(Integer code) {
        if (null == code) {//code为null
            return MessageEnum.NOT_ENUM;
        }
        MessageEnum[] values = MessageEnum.values();
        for (MessageEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return MessageEnum.NOT_ENUM;//没找到、
    }
}

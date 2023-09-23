package com.monkey.monkeycourse.constant;

public enum CourseEnum {

    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    COURSE_FREE(0, "课程免费"),
    COURSE_UNFREE(1, "课程收费"),

    COURSE_UNDISCOUNT(0, "课程未打折"),
    COURSE_DISCOUNT(1, "课程已打折"),

    COURSE_SORT(0, "代表默认"),
    COURSE_DOWNSORT(1, "查找课程降序评论列表"),
    COURSE_UPSPRT(2, "课程升序评论列表"),

    IS_TEMP_BARRAGE(0, "临时弹幕"),
    NOT_TEMP_BARRAGE(1, "不是临时弹幕"),

    // 课程评分
    EXTREME_RECOMMEND(5, "特别满意"),
    PUSH_RECOMMEND(4, "力推"),
    RECOMMEND(3, "推荐"),
    MEDIUM_RECOMMEND(2, "一般推荐"),
    NOT_RECOMMEND(1, "不推荐"),

    // 课程评论是否精选
    COURSE_COMMENT_CURATION(1, "精选"),
    COURSE_COMMENT_NOT_CURATION(0, "不精选"),

    // 是否按下键盘
    IS_KEYDOWN(1, "已按下"),
    NOT_KEYDOWN(0, "未按下")

    ;
    private Integer code;
    private String msg;

    CourseEnum(Integer code, String msg) {
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
    public static CourseEnum getCourseEnum(Integer code) {
        if (null == code) {//code为null
            return CourseEnum.NOT_ENUM;
        }
        CourseEnum[] values = CourseEnum.values();
        for (CourseEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CourseEnum.NOT_ENUM;//没找到、
    }
}

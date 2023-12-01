package com.monkey.monkeyUtils.constants;

/**
 * @author: wusihao
 * @date: 2023/11/1 11:10
 * @version: 1.0
 * @description: 举报内容枚举类
 */
public enum ReportContentEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    // 举报类型
    ARTICLE_REPORT(0, "文章举报"),
    QUESTION_REPORT(1, "问答举报"),
    COURSE_REPORT(2, "课程举报"),
    COMMUNITY_REPORT(3, "社区举报"),
    COMMUNITY_ARTICLE_REPORT(4, "社区文章举报"),
    RESOURCE_REPORT(5, "资源举报")
    ;

    private Integer code;
    private String msg;

    ReportContentEnum(Integer code, String msg) {
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
    public static ReportContentEnum getReportContentEnum(Integer code) {
        //code为null
        if (null == code) {
            return ReportContentEnum.NOT_ENUM;
        }
        ReportContentEnum[] values = ReportContentEnum.values();
        for (ReportContentEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return ReportContentEnum.NOT_ENUM;
    }
}

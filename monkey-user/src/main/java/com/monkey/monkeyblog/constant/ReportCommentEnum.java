package com.monkey.monkeyblog.constant;

/**
 * @author: wusihao
 * @date: 2023/11/1 11:10
 * @version: 1.0
 * @description: 举报评论枚举类
 */
public enum ReportCommentEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),
    // 举报类型
    ARTICLE_REPORT(0, "文章评论举报"),
    QUESTION_REPORT(1, "问答评论举报"),
    QUESTION_REPLY_REPORT(2, "问答回复举报"),
    COURSE_REPORT(3, "课程评论举报"),
    COMMUNITY_ARTICLE_REPORT(4, "社区文章评论举报"),
    RESOURCE_REPORT(5, "资源评论举报")
    ;

    private Integer code;
    private String msg;

    ReportCommentEnum(Integer code, String msg) {
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
    public static ReportCommentEnum getReportCommentEnum(Integer code) {
        //code为null
        if (null == code) {
            return ReportCommentEnum.NOT_ENUM;
        }
        ReportCommentEnum[] values = ReportCommentEnum.values();
        for (ReportCommentEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        //没找到、
        return ReportCommentEnum.NOT_ENUM;
    }
}

package com.monkey.monkeysearch.constant;

/**
 * @author: wusihao
 * @date: 2023/11/15 17:59
 * @version: 1.0
 * @description:
 */
public enum SearchExceptionEnum {
    NOT_ENUM(-1, "未找到该异常"),

    SEARCH_TYPE_ERROR(100, "搜索类型错误"),
    // 搜索模块
    BULK_INSERT_ARTICLE(1800, "批量插入文章索引异常"),
    BULK_INSERT_QUESTION(1801, "批量插入问答索引异常"),
    BULK_INSERT_COURSE(1802, "批量插入课程索引异常"),
    BULK_INSERT_COMMUNITY_ARTICLE(1803, "批量插入社区文章异常"),
    BULK_INSERT_COMMUNITY(1804, "批量插入社区异常"),
    BULK_ALL(1805, "批量插入全部索引异常"),
    BULK_ARTICLE_ALL(1806, "批量插入全部索引文章功能失败"),
    BULK_QUESTION_ALL(1807, "批量插入全部索引问答功能失败"),
    BULK_COURSE_ALL(1808, "批量插入全部索引课程功能失败"),
    BULK_COMMUNITY_ARTICLE_ALL(1809, "批量插入全部索引社区文章功能失败"),
    BULK_RESOURCE_ALL(1810, "批量插入全部索引资源功能失败"),

    ;

    private Integer code;
    private String msg;

    SearchExceptionEnum(Integer code, String msg) {
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
    public static SearchExceptionEnum getSearchExceptionEnum(Integer code) {
        if (null == code) {//code为null
            return SearchExceptionEnum.NOT_ENUM;
        }
        SearchExceptionEnum[] values = SearchExceptionEnum.values();
        for (SearchExceptionEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return SearchExceptionEnum.NOT_ENUM;//没找到、
    }
}

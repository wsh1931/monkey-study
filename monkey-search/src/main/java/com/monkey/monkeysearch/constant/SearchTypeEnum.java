package com.monkey.monkeysearch.constant;

/**
 * @author: wusihao
 * @date: 2023/10/13 8:13
 * @version: 1.0
 * @description:
 */
public enum SearchTypeEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    ARTICLE(1, "文章"),
    QUESTION(2, "问答"),
    COURSE(3, "课程"),
    COMMUNITY_ARTICLE(4, "社区文章"),
    RESOURCE(5, "资源"),

    
    ;
    private Integer code;
    private String type;

    SearchTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    //获取指定值枚举类
    public static SearchTypeEnum getSearchTypeByType(String type) {
        //code为null
        if (null == type) {
            return SearchTypeEnum.NOT_ENUM;
        }
        SearchTypeEnum[] values = SearchTypeEnum.values();
        for (SearchTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        //没找到、
        return SearchTypeEnum.NOT_ENUM;
    }
}

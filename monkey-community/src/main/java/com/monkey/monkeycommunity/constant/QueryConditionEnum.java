package com.monkey.monkeycommunity.constant;

/**
 * @author: wusihao
 * @date: 2023/10/5 9:33
 * @version: 1.0
 * @description: 查询条件常量
 */
public enum QueryConditionEnum {
    // 未定义该枚举类
    NOT_ENUM("not_found", "未找到指定枚举类"),
    ALL("all", "全部"),
    TOP("top", "已置顶"),
    CURATION("curation", "已精选"),

    TASK("task", "任务发布"),

    VOTE("vote", "投票发布"),
    WAIT_APPROVAL("wait_approval", "待审核"),
    APPROVAL_SUCCESS("approval_success", "审核通过"),
    APPROVAL_FAIL("approval_fail", "审核失败")

    ;
    private String value;
    private String label;

    QueryConditionEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    //获取指定值枚举类
    public static QueryConditionEnum getCourseEnum(String value) {
        //code为null
        if (null == value) {
            return QueryConditionEnum.NOT_ENUM;
        }
        QueryConditionEnum[] values = QueryConditionEnum.values();
        for (QueryConditionEnum t : values) {
            if (t.value.equals(value)) {
                return t;
            }
        }

        //没找到、
        return QueryConditionEnum.NOT_ENUM;
    }
}

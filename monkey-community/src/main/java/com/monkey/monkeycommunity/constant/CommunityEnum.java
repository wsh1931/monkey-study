package com.monkey.monkeycommunity.constant;

public enum CommunityEnum {

    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    ONE_LEVEL_LABEL(1, "一级标签"),
    TWO_LEVEL_LABEL(2, "二级标签"),

    // 社区加入条件
    NO_RESTRAIN(0, "无限制"),
    MANAGE_AGREE(1, "需要管理员批准后加入"),
    DIRECTIONAL_INVITATION(2, "限制加入，定向邀请"),

    // 评论前是否需要加入社区
    COMMENT_BEFORE_NEED_ADD_COMMUNITY(1, "评论之前需要加入社区"),
    COMMENT_BEFORE_NOT_NEED_ADD_COMMUNITY(0, "评论之前不需要加入社区"),

    // 社区频道关系表
    SUPPORT_SHOW(0, "支持前端展示"),
    NOT_SUPPORT_SHOW(1, "不支持前端展示"),
    SUPPORT_USER_PUBLISH(0, "支持用户发布文章"),
    NOT_SUPPORT_USER_PUBLISH(1, "不支持用户发布文章"),
    SUPPORT_ALL_CHANNEL(0, "支持在全部频道展示"),
    NOT_SUPPORT_ALL_CHANNEL(1, "不支持在全部评到展示"),

    // 申请加入社区
    NOT_ADD_COMMUNITY(0, "未加入社区"),
    ALREADY_ADD_COMMUNITY(1, "已加入社区"),
    APPROVE_EXAMINE(1, "通过审核"),
    REVIEW_PROGRESS(0, "审核中"),
    APPLICATION_FAIL(-1, "申请失败"),

    ALL_SELECTED(1, "全选"),
    NOT_ALL_SELECTED(0, "未全选")

    ;
    private Integer code;
    private String msg;

    CommunityEnum(Integer code, String msg) {
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
    public static CommunityEnum getCourseEnum(Integer code) {
        if (null == code) {//code为null
            return CommunityEnum.NOT_ENUM;
        }
        CommunityEnum[] values = CommunityEnum.values();
        for (CommunityEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CommunityEnum.NOT_ENUM;//没找到、
    }
}

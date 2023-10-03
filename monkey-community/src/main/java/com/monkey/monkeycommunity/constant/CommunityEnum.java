package com.monkey.monkeycommunity.constant;

public enum CommunityEnum {

    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    ONE_LEVEL_LABEL(1, "一级标签"),
    TWO_LEVEL_LABEL(2, "二级标签"),

    // 社区加入条件
    NO_RESTRAIN(0, "无限制"),
    MANAGE_AGREE(1, "需要管理员批准后加入"),

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

    // 是否加入社区加入社区
    NOT_ADD_COMMUNITY(0, "未加入社区"),
    ALREADY_ADD_COMMUNITY(1, "已加入社区"),
    APPROVALING(-1, "审核中"),

    // 申请加入社区
    APPROVE_EXAMINE(1, "通过审核"),
    REVIEW_PROGRESS(0, "审核中"),
    APPLICATION_FAIL(-1, "申请失败"),
    NOT_APPLICATION(-2, "未申请加入社区"),

    ALL_SELECTED(1, "全选"),
    NOT_ALL_SELECTED(0, "未全选"),

    IS_TASK(1, "以任务形式发布"),
    NOT_TASK(0, "不以任务形式发布"),

    IS_VOTE(1, "已投票形式发布"),
    NOT_VOTE(0, "不以投票形式发布"),

    // 鼠标是否悬浮在显示更多图标上
    IS_HOVER(1, "悬浮"),
    NOT_HOVER(0, "未悬浮"),

    // 是否置顶
    IS_TOP(1, "置顶"),
    NOT_TOP(0, "不置顶"),

    // 官方是否推荐
    IS_RECOMMEND(1, "推荐"),
    NOT_RECOMMEND(0, "不推荐"),

    // 是否精选
    IS_EXCELLENT(1, "精选"),
    NOT_EXCELLENT(0, "不精选"),

    // 是否为社区管理员
    IS_MANAGER(1, "是社区管理员"),
    NOT_MANAGER(0, "不是社区管理员"),

    // 该用户是否在此社区
    IN_COMMUNITY(1, "在社区"),
    NOT_COMMUNITY(0, "不在社区"),

    // 是否参与投票
    IS_PARTICIPATE_VOTE(1, "已参加投票"),
    NOT_PARTICIPATE_VOTE(0, "未参加投票"),

    // 投票是否过期
    NOT_OVERDUE(0, "未过期"),
    IS_OVERDUE(1, "已过期"),

    // 投票类型
    RADIO(0, "单选"),
    MULTI(1, "多选"),

    // 社区文章任务是否公开
    IS_PUBLIC(1, "公开"),
    NOT_PUBLIC(0, "不公开"),

    // 是否点赞
    IS_LIKE(1, "已点赞"),
    NOT_LIKE(0, "未点赞"),

    // 是否被选中
    IS_SELECTED(1, "被选中"),
    NOT_SELECTED(0, "未被选中"),

    // 判断当前登录用户是否是文章作者
    IS_AUTHOR(1, "是作者"),
    NOT_AUTHOR(0, "不是作者"),

    // 是否按下键盘
    IS_KEYDOWN(1, "按下键盘"),
    NOT_KEYDOWN(0, "没按下键盘"),

    // 一级评论回复
    ONE_COMMENT_REPLY(1, "一级评论回复"),
    // 多级评论回复
    MORE_COMMENT_REPLY(2, "多级评论回复"),

    // 社区用户邀请
    WAIT_APPROVAL(0, "等待同意"),
    ALREADY_APPROVAL(1, "已同意"),
    ALREADY_REFUSE(-1, "已拒绝"),

    IS_PRIME_MANAGE(1, "主管理员"),
    IS_MANAGE(0, "管理员"),

    // 评论前是否需要加入社区
    NOT_NEED_ADD_COMMUNITY(0, "不需要"),
    NEED_ADD_COMMUNITY(1, "需要"),

    // 头衔是否保存
    DOWN_NAME_IS_PRESERVE(1, "头衔已保存"),
    DOWN_NAME_NOT_PRESERVE(0, "头衔未保存"),
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

package com.monkey.monkeyresource.constant;

/**
 * @author: wusihao
 * @date: 2023/10/13 8:13
 * @version: 1.0
 * @description:
 */
public enum ResourcesEnum {
    // 未定义该枚举类
    NOT_ENUM(-1, "未找到指定枚举类"),

    // 资源是否精选
    NOT_CURATION(0, "未被精选"),
    IS_CURATION(1, "已精选"),

    // 资源是否打折
    IS_DISCOUNT(1, "已打折"),
    NOT_DISCOUNT(0, "未打折"),

    // 资源是否通过审核
    SUCCESS(1, "已通过审核"),
    FAIL(-1, "未通过审核"),
    REVIEWING(0, "审核中"),

    // 是否有资格下载此资源
    IS_AUTHORIZATION(1, "有权限"),
    NOT_AUTHORIZATION(0, "无权限"),

    IS_KEYDOWN(1, "按下键盘"),
    NOT_KEYDOWN(0, "没按下键盘"),

    // 是否点赞
    IS_LIKE(1, "已点赞"),
    NOT_LIKE(0, "未点赞"),

    // 是否被选中
    IS_SELECTED(1, "被选中"),
    NOT_SELECTED(0, "未被选中"),

    // 鼠标是否悬浮在显示更多图标上
    IS_HOVER(1, "悬浮"),
    NOT_HOVER(0, "未悬浮"),

    // 是否为当前资源作者
    is_Author(1, "是作者"),
    NOT_Author(0, "不是作者"),

    // 评论是否置顶
    COMMENT_IS_TOP(1, "评论已置顶"),
    COMMENT_NOT_TOP(0, "评论未置顶"),
    // 评论是否精选
    COMMENT_IS_CURATION(1, "评论已精选"),
    COMMENT_NOT_CURATION(0, "评论未精选"),
    COMMENT_IS_LIKE(1, "评论已点赞"),
    COMMENT_NOT_LIKE(0, "评论未点赞"),

    RESOURCE_IS_LIKE(1, "资源已点赞"),
    RESOURCE_NOT_LIKE(0, "资源未点赞"),

    RESOURCE_IS_COLLECT(1, "资源已收藏"),
    RESOURCE_NOT_COLLECT(0, "资源未收藏")
    ;
    private Integer code;
    private String msg;

    ResourcesEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

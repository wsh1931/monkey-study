package com.monkey.monkeycommunity.constant;

/**
 * @author: wusihao
 * @date: 2023/9/10 16:22
 * @version: 1.0
 * @description: 社区频道常量类
 */
public enum CommunityChannelEnum {
    // 未定义该枚举类
    NOT_ENUM(-2L, "未找到指定枚举类"),
    ALL(-1L, "全部")


            ;
    private Long sort;
    private String channelName;

    CommunityChannelEnum(Long sort, String channelName) {
        this.sort = sort;
        this.channelName = channelName;
    }

    public Long getSort() {
        return sort;
    }

    public String getChannelName() {
        return channelName;
    }

    //获取指定值枚举类
    public static CommunityChannelEnum getCommunityChannel(Long code) {
        if (null == code) {//code为null
            return CommunityChannelEnum.NOT_ENUM;
        }
        CommunityChannelEnum[] values = CommunityChannelEnum.values();
        for (CommunityChannelEnum value : values) {
            if (value.sort.equals(code)) {
                return value;
            }
        }
        return CommunityChannelEnum.NOT_ENUM;//没找到、
    }
}

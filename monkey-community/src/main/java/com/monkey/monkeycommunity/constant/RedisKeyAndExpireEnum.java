package com.monkey.monkeycommunity.constant;

// -1表示时间永不过期
public enum RedisKeyAndExpireEnum {
    // 我加入的社区数 + userId(天)
    MY_ADD_COMMUNITY_COUNT(1, "myAddCommunityCount："),

    // 我管理的社区数 + userId(天)
    MY_MANAGE_COMMUNITY_COUNT(1, "myManageCommunityCount："),
    // 官方推荐社区数
    RECOMMEND_COMMUNITY_COUNT(-1, "recommendCommunityCount"),

    // 其他社区数
    OTHER_COMMUNITY_COUNT(-1, "otherCommunityCount"),

    // 我加入的社区集合 + userId(天)
    MY_ADD_COMMUNITY_LIST(1, "myAddCommunityList："),

    // 我管理的社区集合 + userId（天）
    MY_MANAGE_COMMUNITY_LIST(1, "myManageCommunityList："),
    // 官方推荐社区集合
    RECOMMEND_COMMUNITY_LIST(-1, "recommendCommunityList"),

    // 其他社区集合
    OTHER_COMMUNITY_LIST(-1, "otherCommunityList"),

    // 社区频道列表(分钟) + communityId
    COMMUNITY_CHANNEL_LIST(10, "communityChannelList：")


    ;

    private Integer timeUnit;
    private String keyName;

    RedisKeyAndExpireEnum(Integer timeUnit, String keyName) {
        this.timeUnit = timeUnit;
        this.keyName = keyName;
    }

    public Integer getTimeUnit() {
        return timeUnit;
    }

    public String getKeyName() {
        return keyName;
    }
}

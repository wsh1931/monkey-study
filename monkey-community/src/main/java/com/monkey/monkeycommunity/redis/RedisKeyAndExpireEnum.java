package com.monkey.monkeycommunity.redis;

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
    COMMUNITY_CHANNEL_LIST(10, "communityChannelList："),

    // 社区基本信息 + communityId
    COMMUNITY_BASE_INFO(10, "communityBaseInfo："),

    // 社区标签信息 + communityId(分钟)
    COMMUNITY_LABEL_LIST(10, "communityLabelList："),

    // 社区管理员列表 + communityId(分钟)
    COMMUNITY_MANAGER_LIST(10, "communityManagerList："),

    // 社区游览数排行列表（天）
    COMMUNITY_VIEW_COUNT_RANK(1, "communityViewCountRank"),

    // 社区成员数排行列表（天）
    COMMUNITY_MEMBER_COUNT_RANK(1, "communityMemberCountRank"),

    // 社区文章数排行列表（天）
    COMMUNITY_ARTICLE_COUNT_RANK(1, "communityArticleCountRank"),

    // 社区点赞排行列表（天）
    COMMUNITY_LIKE_COUNT_RANK(1, "communityLikeCountRank"),

    // 社区评论排行列表（天）
    COMMUNITY_COMMENT_COUNT_RANK(1, "communityCommentCountRank"),

    // 社区评分排行列表（天）
    COMMUNITY_SCORE_COUNT_RANK(1, "communityScoreCountRank"),

    // 社区收藏数排行列表（天）
    COMMUNITY_COLLECT_COUNT_RANK(1, "communityCollectRank"),
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

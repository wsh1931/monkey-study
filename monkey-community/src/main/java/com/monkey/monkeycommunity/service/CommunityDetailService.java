package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityDetailService {
    // 得到我加入的社区数量
    R queryMyAddCommunityCount(String userId);

    // 得到我管理的社区数量
    R queryMyManageCommunityCount(String userId);
    // 得到官方推荐的社区数量
    R queryRecommendCommunityCount();

    // 得到其他社区数
    R queryOtherCommunityCount();
    // 查询我加入的社区集合
    R queryMyAddCommunityList(String userId);

    // 查询我管理的社区集合
    R queryMyManegeCommunityList(String userId);

    // 查询官方推荐社区集合
    R queryRecommendCommunityList();

    // 查询其他社区集合
    R queryOtherCommunityListList();

    // 通过社区名模糊搜索社区信息
    R searchCommunityByCommunityName(String communityName);

    // 得到最新文章列表
    R queryLatestArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到热文章列表
    R queryHottestArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到评分降序列表
    R queryScoreArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到阅读量文章列表
    R queryViewsArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到精选文章列表
    R queryExcellentArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到置顶文章列表
    R queryTopArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到点赞排序文章列表
    R queryLikeArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到收藏排序文章列表
    R queryCollectArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId);

    // 得到我的文章列表
    R queryWithMeCommunityListByCommunityIdAndChannelId(Long currentPage, Long pageSize, Long communityId, String channelName, Long userId, String channelId);

    // 通过搜索字段模糊搜索文章标题
    R searchArticleContent(String title, Long communityId, Long currentPage, Long pageSize);

    // 查询社区频道集合
    R queryCommunityChannel(Long communityId);

    // 判断社区是否存在
    R judgeCommunityIsExist(Long communityId);
}

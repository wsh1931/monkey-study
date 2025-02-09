package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;

import java.util.List;

public interface CommunityService {
    // 得到一级标签
    R getOneLevelLabelList();

    // 通过一级标签id得到二级标签列表
    R getTwoLabelListByOneLabelId(long labelOneId);

    // 得到与我有关社区文章列表
    R queryWithMeArticleList(long userId, int currentPage, int pageSize);

    // 得到点赞最多文章列表
    R queryLikeArticleList(int currentPage, int pageSize);

    // 得到回复最多文章列表
    R queryReplyArticleList(int currentPage, int pageSize);

    // 得到游览最多文章列表
    R queryViewArticleList(int currentPage, int pageSize);

    // 得到收藏最高文章列表
    R queryCollectArticleList(int currentPage, int pageSize);

    // 得到最热文章列表
    R queryHireArticleList(int currentPage, int pageSize);

    // 得到最新文章列表
    R queryLatestArticleList(int currentPage, int pageSize);

    // 通过搜索字段得到一级标签
    R queryOneLabel(String search);

    // 通过条件搜索文章
    R searchArticle(String communityName, List<CommunityClassificationLabel> communityClassificationLabelList, int currentPage, int pageSize);

    // 查询热门社区列表
    R queryHireCommunityList(String userId);

    // 查询最新社区列表
    R queryLatestCommunityList(String userId);

    // 查询我的社区列表
    R queryWithMeCommunityList(long userId);

    // 加入社区实现
    R applicationAddCommunity(long userId, Community community);

    // 退出社区实现
    R turnOutCommunity(long userId, Long communityId);

    List<CommunityArticle> getCommunityArticle(List<CommunityArticle> communityArticleList);

    // 社区文章游览数 + 1
    R communityArticleViewCountAddOne(Long communityArticleId);

    // 查询用户管理社区集合
    R queryUserManageCommunity(String userId, Long currentPage, Integer pageSize);
}

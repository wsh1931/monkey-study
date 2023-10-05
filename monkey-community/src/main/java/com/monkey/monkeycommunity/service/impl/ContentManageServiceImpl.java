package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityChannelEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.QueryConditionEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityChannelMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityChannel;
import com.monkey.monkeycommunity.service.manage.ContentManageService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/5 9:15
 * @version: 1.0
 * @description:
 */
@Service
public class ContentManageServiceImpl implements ContentManageService {
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 通过条件查询内容管理集合
     *
     * @param nowUserId 当前登录用户id
     * @param communityId 社区id
     * @param status 查询状态条件
     * @param channel 查询频道条件
     * @param publisherId 发布者id条件
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 9:29
     */
    @Override
    public R queryContentManageListByCondition(long nowUserId, Long communityId, String status, String channel, String publisherId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.like(publisherId != null && !"".equals(publisherId), "user_id", publisherId);
        if (!channel.equals(CommunityChannelEnum.ALL.getChannelName())) {
            String []split = channel.split(",");
            Long channelId = Long.parseLong(split[0]);
            String channelName = split[1];
            communityArticleQueryWrapper.eq(!CommunityChannelEnum.ALL.getChannelName().equals(channelName), "channel_id", channelId);
        }

        QueryConditionEnum courseEnum = QueryConditionEnum.getCourseEnum(status);
        switch (courseEnum) {
            case TOP:
                communityArticleQueryWrapper.eq("is_top", CommunityEnum.IS_TOP.getCode());
                break;
            case CURATION:
                communityArticleQueryWrapper.eq("is_excellent", CommunityEnum.IS_EXCELLENT.getCode());
                break;
            case WAIT_APPROVAL:
                communityArticleQueryWrapper.eq("status", CommunityEnum.WAIT_APPROVAL.getCode());
                break;
            case APPROVAL_SUCCESS:
                communityArticleQueryWrapper.eq("status", CommunityEnum.ALREADY_APPROVAL.getCode());
                break;
            case APPROVAL_FAIL:
                communityArticleQueryWrapper.eq("status", CommunityEnum.ALREADY_REFUSE.getCode());
                break;
            case TASK:
                communityArticleQueryWrapper.eq("is_task", CommunityEnum.IS_TASK.getCode());
                break;
            case VOTE:
                communityArticleQueryWrapper.eq("is_vote", CommunityEnum.IS_VOTE.getCode());
                break;
        }

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> communityArticleList = selectPage.getRecords();
        for (CommunityArticle communityArticle : communityArticleList) {
            // 查询频道信息
            Long articleChannelId = communityArticle.getChannelId();
            CommunityChannel communityChannel = communityChannelMapper.selectById(articleChannelId);
            communityArticle.setChannelName(communityChannel.getChannelName());

            // 查询发布者信息
            Long articleUserId = communityArticle.getUserId();
            User user = userMapper.selectById(articleUserId);
            communityArticle.setUsername(user.getUsername());
            communityArticle.setUserHeadImg(user.getPhoto());
        }

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 判断社区文章是否存在
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 14:11
     */
    @Override
    public R judgeCommunityArticleIsExist(Long communityArticleId) {
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        if (communityArticle == null) {
            return R.error();
        }
        return R.ok();
    }
}

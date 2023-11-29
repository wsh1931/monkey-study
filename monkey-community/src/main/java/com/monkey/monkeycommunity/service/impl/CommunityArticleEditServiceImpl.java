package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.monkey.monkeyUtils.constants.DefaultPictureEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityChannelMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityChannel;
import com.monkey.monkeycommunity.service.CommunityArticleEditService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/28 17:23
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityArticleEditServiceImpl implements CommunityArticleEditService {
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    /**
     * 查询社区文章信息
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/27 11:39
     */
    @Override
    public R queryCommunityArticle(Long communityArticleId) {
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        LambdaQueryWrapper<CommunityArticle> communityArticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleLambdaQueryWrapper.eq(CommunityArticle::getStatus, CommunityEnum.WAIT_APPROVAL.getCode());
        Long articleChannelId = communityArticle.getChannelId();
        CommunityChannel communityChannel = communityChannelMapper.selectById(articleChannelId);
        communityArticle.setChannelName(communityChannel.getChannelName());
        return R.ok(communityArticle);
    }

    /**
     * 删除数据库中的图片
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/27 16:22
     */
    @Override
    public R deleteCommunityArticlePicture(Long communityArticleId) {
        LambdaUpdateWrapper<CommunityArticle> communityArticleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        communityArticleLambdaUpdateWrapper.eq(CommunityArticle::getId, communityArticleId);
        communityArticleLambdaUpdateWrapper.set(CommunityArticle::getPicture, null);
        communityArticleLambdaUpdateWrapper.set(CommunityArticle::getUpdateTime, new Date());
        communityArticleLambdaUpdateWrapper.set(CommunityArticle::getUpdateUser, JwtUtil.getUserId());
        communityArticleLambdaUpdateWrapper.set(CommunityArticle::getStatus, CommunityEnum.WAIT_APPROVAL.getCode());
        communityArticleMapper.update(null, communityArticleLambdaUpdateWrapper);
        return R.ok(null, TipConstant.uploadCommunityArticleSuccessWaitApproval);
    }

    /**
     * 更新社区文章
     *
     * @param communityArticle 社区文章实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/27 16:46
     */
    @Override
    public R updateCommunityArticle(CommunityArticle communityArticle) {
        if (communityArticle.getPicture() == null) {
            communityArticle.setPicture(DefaultPictureEnum.COMMUNITY_ARTICLE.getUrl());
        }
        Long aLong = Long.parseLong(JwtUtil.getUserId());
        communityArticle.setUpdateUser(aLong);
        communityArticle.setUpdateTime(new Date());
        communityArticle.setStatus(CommunityEnum.WAIT_APPROVAL.getCode());
        LambdaUpdateWrapper<CommunityArticle> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(CommunityArticle::getId, communityArticle.getId());
        communityArticleMapper.update(communityArticle, lambdaUpdateWrapper);
        return R.ok(null, TipConstant.uploadCommunityArticleSuccessWaitApproval);
    }
}

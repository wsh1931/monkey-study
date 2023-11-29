package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.DefaultPictureEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.feign.CommunityToSearchFeign;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.service.UserHomeCommunityArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/27 9:34
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class UserHomeCommunityArticleServiceImpl implements UserHomeCommunityArticleService {
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityArticleTaskMapper communityArticleTaskMapper;
    @Resource
    private CommunityArticleTaskReplyMapper communityArticleTaskReplyMapper;
    @Resource
    private CommunityArticleVoteMapper communityArticleVoteMapper;
    @Resource
    private CommunityArticleVoteItemMapper communityArticleVoteItemMapper;
    @Resource
    private CommunityArticleVoteUserMapper communityArticleVoteUserMapper;
    @Resource
    private CommunityArticleLikeMapper communityArticleLikeMapper;
    @Resource
    private CommunityArticleScoreMapper communityArticleScoreMapper;
    @Resource
    private CommunityArticleCommentMapper communityArticleCommentMapper;
    @Resource
    private CommunityArticleCommentLikeMapper communityArticleCommentLikeMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private CommunityToSearchFeign communityToSearchFeign;
    /**
     * 通过用户id查询社区文章集合
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/27 9:37
     */
    @Override
    public R queryCommunityArticleByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<CommunityArticle> communityArticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleLambdaQueryWrapper.eq(CommunityArticle::getUserId, userId);
        communityArticleLambdaQueryWrapper.orderByDesc(CommunityArticle::getCreateTime);
        Page page = new Page(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleLambdaQueryWrapper);
        List<CommunityArticle> communityArticleList = selectPage.getRecords();
        communityArticleList.forEach(communityArticle -> {
            Long communityId = communityArticle.getCommunityId();
            Community community = communityMapper.selectById(communityId);
            communityArticle.setCommunityName(community.getName());
        });
        return R.ok(selectPage);
    }

    /**
     * 删除社区文章
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/28 9:10
     */
    @Override
    public R deleteCommunityArticle(Long communityArticleId) {
        // 删除社区文章id
        communityArticleMapper.deleteById(communityArticleId);
        // 得到社区文章任务表id
        LambdaQueryWrapper<CommunityArticleTask> communityArticleTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleTaskLambdaQueryWrapper.eq(CommunityArticleTask::getCommunityArticleId, communityArticleId)
                .select(CommunityArticleTask::getId);
        CommunityArticleTask communityArticleTask = communityArticleTaskMapper.selectOne(communityArticleTaskLambdaQueryWrapper);
        if (communityArticleTask != null) {
            // 删除社区文章任务表
            communityArticleTaskMapper.deleteById(communityArticleTask);

            // 删除社区文章任务回复表
            LambdaQueryWrapper<CommunityArticleTaskReply> communityArticleTaskReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleTaskReplyLambdaQueryWrapper.eq(CommunityArticleTaskReply::getCommunityArticleTaskId, communityArticleTask.getId());
            communityArticleTaskReplyMapper.delete(communityArticleTaskReplyLambdaQueryWrapper);
        }

        // 得到文章任务投票表id集合
        LambdaQueryWrapper<CommunityArticleVote> communityArticleVoteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleVoteLambdaQueryWrapper.eq(CommunityArticleVote::getCommunityArticleId, communityArticleId)
                .select(CommunityArticleVote::getId);
        CommunityArticleVote communityArticleVote = communityArticleVoteMapper.selectOne(communityArticleVoteLambdaQueryWrapper);
        if (communityArticleVote != null) {
            // 删除任务文章投票表
            LambdaQueryWrapper<CommunityArticleVote> deleteCommunityArticleVote = new LambdaQueryWrapper<>();
            Long communityArticleVoteId = communityArticleVote.getId();
            deleteCommunityArticleVote.eq(CommunityArticleVote::getId, communityArticleVoteId);
            communityArticleVoteMapper.delete(deleteCommunityArticleVote);

            // 得到社区文章选项表id集合
            LambdaQueryWrapper<CommunityArticleVoteItem> communityArticleVoteItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleVoteItemLambdaQueryWrapper.eq(CommunityArticleVoteItem::getCommunityArticleVoteId, communityArticleVoteId)
                    .select(CommunityArticleVoteItem::getId);
            List<Object> communityArticleVoteItemIdList = communityArticleVoteItemMapper.selectObjs(communityArticleVoteItemLambdaQueryWrapper);
            if (communityArticleVoteItemIdList != null && communityArticleVoteItemIdList.size() > 0) {
                // 删除社区文章投票选项表
                LambdaQueryWrapper<CommunityArticleVoteItem> deleteCommunityArticleVoteItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deleteCommunityArticleVoteItemLambdaQueryWrapper.eq(CommunityArticleVoteItem::getId, communityArticleVoteId);
                communityArticleVoteItemMapper.delete(deleteCommunityArticleVoteItemLambdaQueryWrapper);

                // 删除社区文章投票用户表
                LambdaQueryWrapper<CommunityArticleVoteUser> communityArticleVoteUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                communityArticleVoteUserLambdaQueryWrapper.in(CommunityArticleVoteUser::getCommunityArticleVoteItemId, communityArticleVoteItemIdList);
                communityArticleVoteUserMapper.delete(communityArticleVoteUserLambdaQueryWrapper);
            }
        }

        // 删除社区文章点赞表
        LambdaQueryWrapper<CommunityArticleLike> communityArticleLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleLikeLambdaQueryWrapper.eq(CommunityArticleLike::getCommunityArticleId, communityArticleId);
        communityArticleLikeMapper.delete(communityArticleLikeLambdaQueryWrapper);

        // 删除社区文章评分表
        LambdaQueryWrapper<CommunityArticleScore> communityArticleScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleScoreLambdaQueryWrapper.eq(CommunityArticleScore::getCommunityArticleId, communityArticleId);
        communityArticleScoreMapper.delete(communityArticleScoreLambdaQueryWrapper);

        // 得到社区文章评论id
        LambdaQueryWrapper<CommunityArticleComment> communityArticleCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleCommentLambdaQueryWrapper.eq(CommunityArticleComment::getCommunityArticleId, communityArticleId)
                .select(CommunityArticleComment::getId);
        List<Object> communityArticleCommentIdList = communityArticleCommentMapper.selectObjs(communityArticleCommentLambdaQueryWrapper);
        if (communityArticleCommentIdList != null && communityArticleCommentIdList.size() > 0) {
            // 删除社区文章评论表
            LambdaQueryWrapper<CommunityArticleComment> deleteCommunityArticleCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deleteCommunityArticleCommentLambdaQueryWrapper.in(CommunityArticleComment::getId, communityArticleCommentIdList);
            communityArticleCommentMapper.delete(deleteCommunityArticleCommentLambdaQueryWrapper);

            // 删除社区文章评论点赞表
            LambdaQueryWrapper<CommunityArticleCommentLike> communityArticleCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleCommentLikeLambdaQueryWrapper.in(CommunityArticleCommentLike::getCommunityArticleCommentId, communityArticleCommentIdList);
            communityArticleCommentLikeMapper.delete(communityArticleCommentLikeLambdaQueryWrapper);
        }

        // 删除社区文章收藏表
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getAssociateId, communityArticleId)
                .eq(CollectContentConnect::getType, CommonEnum.COLLECT_COMMUNITY_ARTICLE.getCode());
        collectContentConnectMapper.delete(collectContentConnectLambdaQueryWrapper);

        communityToSearchFeign.deleteCommunityArticle(String.valueOf(communityArticleId));
        return R.ok();
    }
}

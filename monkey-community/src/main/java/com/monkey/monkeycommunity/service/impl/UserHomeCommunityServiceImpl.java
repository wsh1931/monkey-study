package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuConnectMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.pojo.CommunityManage;
import com.monkey.monkeyUtils.pojo.CommunityManageMenu;
import com.monkey.monkeyUtils.pojo.CommunityManageMenuConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.feign.CommunityToSearchFeign;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.pojo.vo.CommunityVo;
import com.monkey.monkeycommunity.service.UserHomeCommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/25 14:54
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class UserHomeCommunityServiceImpl implements UserHomeCommunityService {
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    CommunityUserApplicationMapper communityUserApplicationMapper;
    @Resource
    private CommunityRoleMenuMapper communityRoleMenuMapper;
    @Resource
    private CommunityRoleMenuConnectMapper communityRoleMenuConnectMapper;
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    @Resource
    private CommunityManageMapper communityManageMapper;
    @Resource
    private CommunityManageMenuMapper communityManageMenuMapper;
    @Resource
    private CommunityManageMenuConnectMapper communityManageMenuConnectMapper;
    @Resource
    private CommunityUserInviteMapper communityUserInviteMapper;
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
     * 通过用户id查询社区集合
     *
     * @param userId 用户id
     * @param pageSize 每页数据量
     * @param currentPage 当前页
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/25 14:57
     */
    @Override
    public R queryCommunityByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Community> communityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityLambdaQueryWrapper.eq(Community::getUserId, userId);
        communityLambdaQueryWrapper.orderByDesc(Community::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityMapper.selectPage(page, communityLambdaQueryWrapper);
        List<Community> communityList = selectPage.getRecords();
        List<CommunityVo> communityVoList = new ArrayList<>(communityList.size());
        communityList.stream().forEach(community -> {
            CommunityVo communityVo = new CommunityVo();
            BeanUtils.copyProperties(community, communityVo);
            communityVoList.add(communityVo);
        });

        selectPage.setRecords(communityVoList);
        return R.ok(selectPage);
    }

    /**
     * 删除社区
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/25 15:34
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteCommunity(Long communityId) {

        log.info("删除社区 communityId = {}", communityId);
        // 删除社区
        communityMapper.deleteById(communityId);

        // 删除社区标签关联表
        LambdaQueryWrapper<CommunityLabelConnect> communityLabelConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityLabelConnectLambdaQueryWrapper.eq(CommunityLabelConnect::getCommunityId, communityId);
        communityLabelConnectMapper.delete(communityLabelConnectLambdaQueryWrapper);

        // 删除社区频道表
        LambdaQueryWrapper<CommunityChannel> communityChannelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityChannelLambdaQueryWrapper.eq(CommunityChannel::getCommunityId, communityId);
        communityChannelMapper.delete(communityChannelLambdaQueryWrapper);

        // 删除用户申请表
        LambdaQueryWrapper<CommunityUserApplication> communityUserApplicationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityUserApplicationLambdaQueryWrapper.eq(CommunityUserApplication::getCommunityId, communityId);
        communityUserApplicationMapper.delete(communityUserApplicationLambdaQueryWrapper);

        // 删除与社区角色相关的表
        LambdaQueryWrapper<CommunityUserRoleConnect> communityUserRoleConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityUserRoleConnectLambdaQueryWrapper.eq(CommunityUserRoleConnect::getCommunityId, communityId)
                                                  .select(CommunityUserRoleConnect::getRoleId);
        // 1: 得到社区角色id集合
        List<Object> communityRoleIdList = communityUserRoleConnectMapper.selectObjs(communityUserRoleConnectLambdaQueryWrapper);
        if (communityRoleIdList != null && communityRoleIdList.size() > 0) {
        // 2: 删除社区角色关系表
        LambdaQueryWrapper<CommunityUserRoleConnect> deleteCommunityUserRoleConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deleteCommunityUserRoleConnectLambdaQueryWrapper.eq(CommunityUserRoleConnect::getCommunityId, communityId);
        communityUserRoleConnectMapper.delete(deleteCommunityUserRoleConnectLambdaQueryWrapper);
        // 3: 通过社区角色id集合得到社区角色权限id集合
            LambdaQueryWrapper<CommunityRoleMenuConnect> communityRoleMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityRoleMenuConnectLambdaQueryWrapper.in(CommunityRoleMenuConnect::getRoleId, communityRoleIdList)
                    .select(CommunityRoleMenuConnect::getMenuId);
            List<Object> communityRoleMenuIdList = communityRoleMenuConnectMapper.selectObjs(communityRoleMenuConnectLambdaQueryWrapper);
            // 4: 删除社区角色权限关联表
            LambdaQueryWrapper<CommunityRoleMenuConnect> deleteCommunityRoleMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deleteCommunityRoleMenuConnectLambdaQueryWrapper.in(CommunityRoleMenuConnect::getRoleId, communityRoleIdList);
            communityRoleMenuConnectMapper.delete(deleteCommunityRoleMenuConnectLambdaQueryWrapper);
            // 5: 删除社区角色表
            LambdaQueryWrapper<CommunityRole> communityRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityRoleLambdaQueryWrapper.in(CommunityRole::getId, communityRoleIdList);
            communityRoleMapper.delete(communityRoleLambdaQueryWrapper);
            // 6: 删除角色社区权限表
            if (communityRoleMenuIdList != null && communityRoleMenuIdList.size() > 0) {
                LambdaQueryWrapper<CommunityRoleMenu> communityRoleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
                communityRoleMenuLambdaQueryWrapper.in(CommunityRoleMenu::getId, communityRoleMenuIdList);
                communityRoleMenuMapper.delete(communityRoleMenuLambdaQueryWrapper);
            }
            // 7: 删除社区角色关联表
            LambdaQueryWrapper<CommunityRoleConnect> communityRoleConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityRoleConnectLambdaQueryWrapper.eq(CommunityRoleConnect::getCommunityId, communityId);
            communityRoleConnectMapper.delete(communityRoleConnectLambdaQueryWrapper);
        }



        // 删除与社区管理相关的表
        // 1: 得到社区管理员关联表id集合
        LambdaQueryWrapper<CommunityManage> communityManageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityManageLambdaQueryWrapper.eq(CommunityManage::getCommunityId, communityId)
                                        .select(CommunityManage::getId);
        List<Object> communityManageIdList = communityManageMapper.selectObjs(communityManageLambdaQueryWrapper);
        if (communityManageIdList != null && communityManageIdList.size() > 0) {
            // 2: 删除社区管理员关联表
            LambdaQueryWrapper<CommunityManage> deleteCommunityManageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deleteCommunityManageLambdaQueryWrapper.in(CommunityManage::getId, communityManageIdList);
            communityManageMapper.delete(deleteCommunityManageLambdaQueryWrapper);
            // 3: 得到社区管理员权限表id
            LambdaQueryWrapper<CommunityManageMenuConnect> communityManageMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityManageMenuConnectLambdaQueryWrapper.in(CommunityManageMenuConnect::getCommunityManageId, communityManageIdList)
                    .select(CommunityManageMenuConnect::getCommunityManageMenuId);
            List<Object> communityManageMenuIdList = communityManageMenuConnectMapper.selectObjs(communityManageMenuConnectLambdaQueryWrapper);
            if (communityManageMenuIdList != null && communityManageMenuIdList.size() > 0) {
                // 4: 删除社区管理员权限关联表
                LambdaQueryWrapper<CommunityManageMenuConnect> deleteCommunityManageMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deleteCommunityManageMenuConnectLambdaQueryWrapper.in(CommunityManageMenuConnect::getCommunityManageId, communityManageIdList);
                communityManageMenuConnectMapper.delete(deleteCommunityManageMenuConnectLambdaQueryWrapper);
                // 5: 删除社区管理员权限表
                LambdaQueryWrapper<CommunityManageMenu> deleteCommunityManageMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deleteCommunityManageMenuLambdaQueryWrapper.in(CommunityManageMenu::getId, communityManageMenuIdList);
                communityManageMenuMapper.delete(deleteCommunityManageMenuLambdaQueryWrapper);
            }
        }

        // 删除社区用户邀请表
        LambdaQueryWrapper<CommunityUserInvite> communityUserInviteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityUserInviteLambdaQueryWrapper.eq(CommunityUserInvite::getCommunityId, communityId);
        communityUserInviteMapper.delete(communityUserInviteLambdaQueryWrapper);

        // 得到社区文章id集合
        LambdaQueryWrapper<CommunityArticle> communityArticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleLambdaQueryWrapper.eq(CommunityArticle::getCommunityId, communityId)
                                        .select(CommunityArticle::getId);

        List<Object> communityArticleIdList = communityArticleMapper.selectObjs(communityArticleLambdaQueryWrapper);
        if (communityArticleIdList != null && communityArticleIdList.size() > 0) {
            // 删除社区文章id
            LambdaQueryWrapper<CommunityArticle> deleteCommunityArticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deleteCommunityArticleLambdaQueryWrapper.in(CommunityArticle::getId, communityArticleIdList);
            communityArticleMapper.delete(deleteCommunityArticleLambdaQueryWrapper);
            // 得到社区文章任务表id
            LambdaQueryWrapper<CommunityArticleTask> communityArticleTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleTaskLambdaQueryWrapper.in(CommunityArticleTask::getCommunityArticleId, communityArticleIdList)
                    .select(CommunityArticleTask::getId);
            List<Object> communityArticleTaskIdList = communityArticleTaskMapper.selectObjs(communityArticleTaskLambdaQueryWrapper);
            if (communityArticleTaskIdList != null && communityArticleTaskIdList.size() > 0) {
                // 删除社区文章任务表
                LambdaQueryWrapper<CommunityArticleTask> deleteCommunityArticleTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deleteCommunityArticleTaskLambdaQueryWrapper.in(CommunityArticleTask::getId, communityArticleTaskIdList);
                communityArticleTaskMapper.delete(communityArticleTaskLambdaQueryWrapper);

                // 删除社区文章任务回复表
                LambdaQueryWrapper<CommunityArticleTaskReply> communityArticleTaskReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                communityArticleTaskReplyLambdaQueryWrapper.in(CommunityArticleTaskReply::getCommunityArticleTaskId, communityArticleTaskIdList);
                communityArticleTaskReplyMapper.delete(communityArticleTaskReplyLambdaQueryWrapper);
            }

            // 得到文章任务投票表id集合
            LambdaQueryWrapper<CommunityArticleVote> communityArticleVoteLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleVoteLambdaQueryWrapper.in(CommunityArticleVote::getCommunityArticleId, communityArticleIdList)
                    .select(CommunityArticleVote::getId);
            List<Object> communityArticleVoteIdList = communityArticleVoteMapper.selectObjs(communityArticleVoteLambdaQueryWrapper);
            if (communityArticleVoteIdList != null && communityArticleVoteIdList.size() > 0) {
                // 删除任务文章投票表
                LambdaQueryWrapper<CommunityArticleVote> deleteCommunityArticleVote = new LambdaQueryWrapper<>();
                deleteCommunityArticleVote.in(CommunityArticleVote::getId, communityArticleVoteIdList);
                communityArticleVoteMapper.delete(deleteCommunityArticleVote);

                // 得到社区文章选项表id集合
                LambdaQueryWrapper<CommunityArticleVoteItem> communityArticleVoteItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
                communityArticleVoteItemLambdaQueryWrapper.in(CommunityArticleVoteItem::getCommunityArticleVoteId, communityArticleVoteIdList)
                        .select(CommunityArticleVoteItem::getId);
                List<Object> communityArticleVoteItemIdList = communityArticleVoteItemMapper.selectObjs(communityArticleVoteItemLambdaQueryWrapper);
                if (communityArticleVoteItemIdList != null && communityArticleVoteItemIdList.size() > 0) {
                    // 删除社区文章投票选项表
                    LambdaQueryWrapper<CommunityArticleVoteItem> deleteCommunityArticleVoteItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    deleteCommunityArticleVoteItemLambdaQueryWrapper.in(CommunityArticleVoteItem::getId, communityArticleVoteItemIdList);
                    communityArticleVoteItemMapper.delete(deleteCommunityArticleVoteItemLambdaQueryWrapper);

                    // 删除社区文章投票用户表
                    LambdaQueryWrapper<CommunityArticleVoteUser> communityArticleVoteUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    communityArticleVoteUserLambdaQueryWrapper.in(CommunityArticleVoteUser::getCommunityArticleVoteItemId, communityArticleVoteItemIdList);
                    communityArticleVoteUserMapper.delete(communityArticleVoteUserLambdaQueryWrapper);
                }
            }

            // 删除社区文章点赞表
            LambdaQueryWrapper<CommunityArticleLike> communityArticleLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleLikeLambdaQueryWrapper.in(CommunityArticleLike::getCommunityArticleId, communityArticleIdList);
            communityArticleLikeMapper.delete(communityArticleLikeLambdaQueryWrapper);

            // 删除社区文章评分表
            LambdaQueryWrapper<CommunityArticleScore> communityArticleScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleScoreLambdaQueryWrapper.in(CommunityArticleScore::getCommunityArticleId, communityArticleIdList);
            communityArticleScoreMapper.delete(communityArticleScoreLambdaQueryWrapper);

            // 得到社区文章评论id
            LambdaQueryWrapper<CommunityArticleComment> communityArticleCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityArticleCommentLambdaQueryWrapper.in(CommunityArticleComment::getCommunityArticleId, communityArticleIdList)
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
            collectContentConnectLambdaQueryWrapper.in(CollectContentConnect::getAssociateId, communityArticleIdList)
                    .eq(CollectContentConnect::getType, CommonEnum.COLLECT_COMMUNITY_ARTICLE.getCode());
            collectContentConnectMapper.delete(collectContentConnectLambdaQueryWrapper);
        }

        communityToSearchFeign.deleteCommunity(communityId);
        return R.ok();
    }
}

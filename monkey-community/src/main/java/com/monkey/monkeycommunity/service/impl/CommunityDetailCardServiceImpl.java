package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityRoleConnect;
import com.monkey.monkeycommunity.service.CommunityDetailCardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/11 10:17
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityDetailCardServiceImpl implements CommunityDetailCardService {
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    /**
     * 判断是否有显示隐藏框的权力
     *
     * @param communityId 社区id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:22
     */
    @Override
    public R judgePower(Long communityId, String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(false);
        }

        // 判断是否是管理员
        Boolean isManagement = juegeIsManager(communityId, userId);
        if (isManagement) {
            return R.ok(true);
        }

        return R.ok(false);
    }

    /**
     * 判断当前登录用户是否是管理员
     *
     * @param communityId 社区id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:59
     */
    private Boolean juegeIsManager(Long communityId, String userId) {
        QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityRoleConnectQueryWrapper.eq("user_id", userId);
        communityRoleConnectQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        communityRoleConnectQueryWrapper.select("role_id");
        CommunityRoleConnect communityRoleConnect = communityRoleConnectMapper.selectOne(communityRoleConnectQueryWrapper);
        if (communityRoleConnect == null) {
            return false;
        }
        Long roleId = communityRoleConnect.getRoleId();
        if (roleId.equals(CommunityRoleEnum.PRIMARY_ADMINISTRATOR.getCode()) || roleId.equals(CommunityRoleEnum.ADMINISTRATOR.getCode())) {
            return true;
        }

        return false;
    }

    /**
     * 删除社区文章
     *
     * @param articleId   文章id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:52
     */
    @Override
    public R deleteArticle(Long articleId, long userId, long communityId) {
        Boolean isManager = juegeIsManager(communityId, String.valueOf(userId));
        if (!isManager) {
            throw new MonkeyBlogException(ExceptionEnum.NOT_POWER.getCode(), ExceptionEnum.NOT_POWER.getMsg());
        }

        communityArticleMapper.deleteById(articleId);
        return R.ok();
    }

    /**
     * 将文章设置为精选内容
     *
     * @param articleId 文章id
     * @param userId 用户id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 11:23
     */
    @Override
    public R setExcellentArticle(Long articleId, Long communityId, String userId) {
        Boolean isManager = juegeIsManager(communityId, userId);
        if (!isManager) {
            throw new MonkeyBlogException(ExceptionEnum.NOT_POWER.getCode(), ExceptionEnum.NOT_POWER.getMsg());
        }

        CommunityArticle communityArticle = communityArticleMapper.selectById(articleId);
        Integer isExcellent = communityArticle.getIsExcellent();
        if (CommunityEnum.IS_EXCELLENT.getCode().equals(isExcellent)) {
            communityArticle.setIsExcellent(CommunityEnum.NOT_EXCELLENT.getCode());
        } else {
            communityArticle.setIsExcellent(CommunityEnum.IS_EXCELLENT.getCode());
        }
        communityArticleMapper.updateById(communityArticle);
        return R.ok();
    }

    /**
     * 将文章设置为置顶内容
     *
     * @param articleId 文章id
     * @param userId 用户id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 11:30
     */
    @Override
    public R setTopArticle(Long articleId, Long communityId, String userId) {
        Boolean isManager = juegeIsManager(communityId, userId);
        if (!isManager) {
            throw new MonkeyBlogException(ExceptionEnum.NOT_POWER.getCode(), ExceptionEnum.NOT_POWER.getMsg());
        }

        CommunityArticle communityArticle = communityArticleMapper.selectById(articleId);
        Integer isTop = communityArticle.getIsTop();
        if (CommunityEnum.IS_TOP.getCode().equals(isTop)) {
            communityArticle.setIsTop(CommunityEnum.NOT_TOP.getCode());
        } else {
            communityArticle.setIsTop(CommunityEnum.IS_TOP.getCode());
        }
        communityArticleMapper.updateById(communityArticle);
        return R.ok();
    }

}

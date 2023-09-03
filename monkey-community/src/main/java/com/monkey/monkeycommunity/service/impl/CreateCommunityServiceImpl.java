package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.service.CreateCommunityService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/2 15:08
 * @version: 1.0
 * @description:
 */
@Service
public class CreateCommunityServiceImpl implements CreateCommunityService {
    @Resource
    private CommunityAttributeMapper communityAttributeMapper;
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;

    /**
     * 得到社区属性列表
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 15:09
     */
    @Override
    public R queryCommunityAttributeList() {
        QueryWrapper<CommunityAttribute> communityAttributeQueryWrapper = new QueryWrapper<>();
        communityAttributeQueryWrapper.orderByAsc("sort");
        communityAttributeQueryWrapper.orderByAsc("create_time");
        return R.ok(communityAttributeMapper.selectList(communityAttributeQueryWrapper));
    }

    /**
     * 得到社区分类列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 15:25
     */
    @Override
    public R queryCommunityClassificationList() {
        QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
        communityClassificationLabelQueryWrapper.eq("level", CommunityEnum.ONE_LEVEL_LABEL.getCode());
        communityClassificationLabelQueryWrapper.orderByAsc("sort");
        communityClassificationLabelQueryWrapper.orderByAsc("create_time");
        return R.ok(communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper));
    }

    /**
     * 创建社区
     *
     * @param community 社区实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 16:10
     */
    @Override
    public R createCommunity(Community community) {
        Date createTime = new Date();
        Date time = createTime;
        community.setCreateTime(time);
        community.setUpdateTime(time);
        String userId = JwtUtil.getUserId();
        community.setUserId(Long.parseLong(userId));
        communityMapper.insert(community);


        // 添加社区标签表
        List<CommunityClassificationLabel> communityClassificationLabelList = community.getCommunityClassificationLabelList();
        for (CommunityClassificationLabel communityClassificationLabel : communityClassificationLabelList) {
            Long communityClassificationLabelId = communityClassificationLabel.getId();
            CommunityLabelConnect communityLabelConnect = new CommunityLabelConnect();
            communityLabelConnect.setCommunityId(community.getId());
            communityLabelConnect.setCommunityClassificationLabelId(communityClassificationLabelId);
            communityLabelConnect.setCreateTime(createTime);
            communityLabelConnectMapper.insert(communityLabelConnect);
        }


        // 添加社区角色表
        CommunityRoleConnect communityRoleConnect = new CommunityRoleConnect();
        communityRoleConnect.setCommunityId(community.getId());
        communityRoleConnect.setRoleId(CommunityRoleEnum.PRIMARY_ADMINISTRATOR.getCode());
        communityRoleConnect.setStatus(CommunityEnum.REVIEW_PROGRESS.getCode());
        communityRoleConnect.setCreateTime(createTime);
        communityRoleConnect.setUdpateTime(createTime);
        communityRoleConnect.setUserId(Long.parseLong(userId));
        communityRoleConnectMapper.insert(communityRoleConnect);
        return R.ok();
    }
}

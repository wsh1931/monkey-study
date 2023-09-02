package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.CommunityAttributeMapper;
import com.monkey.monkeycommunity.mapper.CommunityClassificationLabelMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityAttribute;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;
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
        List<CommunityClassificationLabel> communityClassificationLabelList = community.getCommunityClassificationLabelList();
        String labels = "";
        int len = communityClassificationLabelList.size();
        for (int i = 0; i < len; i ++ ) {
            CommunityClassificationLabel communityClassificationLabel = communityClassificationLabelList.get(i);
            if (i == len - 1) {
                labels += communityClassificationLabel.getName();
            } else {
                labels += communityClassificationLabel.getName() + ",";
            }
        }

        community.setContentLabel(labels);
        Date time = new Date();
        community.setCreateTime(time);
        community.setUpdateTime(time);
        String userId = JwtUtil.getUserId();
        community.setUserId(Long.parseLong(userId));
        communityMapper.insert(community);
        return R.ok();
    }
}

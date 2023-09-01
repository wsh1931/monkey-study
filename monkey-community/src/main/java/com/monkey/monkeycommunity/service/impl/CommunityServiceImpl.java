package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.CommunityClassificationAttributeMapper;
import com.monkey.monkeycommunity.pojo.CommunityClassificationAttribute;
import com.monkey.monkeycommunity.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/9/1 17:37
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityServiceImpl implements CommunityService {
    @Autowired
    private CommunityClassificationAttributeMapper communityClassificationAttributeMapper;
    /**
     * 得到一级标签
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/1 17:44
     */
    @Override
    public R getOneLevelLabelList() {
        QueryWrapper<CommunityClassificationAttribute> communityClassificationAttributeQueryWrapper = new QueryWrapper<>();
        communityClassificationAttributeQueryWrapper.eq("level", CommunityEnum.ONE_LEVEL_LABEL.getCode());
        communityClassificationAttributeQueryWrapper.orderByAsc("sort");
        communityClassificationAttributeQueryWrapper.orderByDesc("create_time");
        return R.ok(communityClassificationAttributeMapper.selectList(communityClassificationAttributeQueryWrapper));
    }

    /**
     * 通过一级标签id得到二级标签列表
     *
     * @param labelOneId 一级标签id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/1 17:59
     */
    @Override
    public R getTwoLabelListByOneLabelId(long labelOneId) {
        QueryWrapper<CommunityClassificationAttribute> communityClassificationAttributeQueryWrapper = new QueryWrapper<>();
        communityClassificationAttributeQueryWrapper.eq("parent_id", labelOneId);
        communityClassificationAttributeQueryWrapper.orderByAsc("sort");
        communityClassificationAttributeQueryWrapper.orderByDesc("create_time");
        return R.ok(communityClassificationAttributeMapper.selectList(communityClassificationAttributeQueryWrapper));
    }
}

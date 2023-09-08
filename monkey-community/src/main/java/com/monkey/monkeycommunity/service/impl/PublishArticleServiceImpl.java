package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.pojo.vo.CommunityArticleVo;
import com.monkey.monkeycommunity.service.PublishArticleService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/6 16:34
 * @version: 1.0
 * @description:
 */
@Service
public class PublishArticleServiceImpl implements PublishArticleService {
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityArticleTaskMapper communityArticleTaskMapper;
    @Resource
    private CommunityArticleVetoMapper communityArticleVetoMapper;
    @Resource
    private CommunityArticleVetoItemMapper communityArticleVetoItemMapper;
    /**
     * 查询社区角色列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/6 16:36
     */
    @Override
    public R queryCommunityRoleList(Long communityId) {
        QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper1 = new QueryWrapper<>();
        communityRoleConnectQueryWrapper1.eq("community_id", communityId);
        communityRoleConnectQueryWrapper1.groupBy("role_id");
        communityRoleConnectQueryWrapper1.select("role_id, count(*) as count");
        List<Map<String, Object>> roleIdList = communityRoleConnectMapper.selectMaps(communityRoleConnectQueryWrapper1);

        // 最终返回集合
        int sum = 0;
        List<CommunityRole> communityRoleList = new ArrayList<>();
        for (Map<String, Object> temp : roleIdList) {
            long roleId = Long.parseLong(temp.get("role_id").toString());
            CommunityRole communityRole = communityRoleMapper.selectById(roleId);
            int count = Integer.parseInt(temp.get("count").toString());
            communityRole.setCount(count);
            sum += count;


            // 通过角色id查询用户列表
            QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("role_id", roleId);
            communityRoleConnectQueryWrapper.eq("community_id", communityId);
            communityRoleConnectQueryWrapper.select("user_id");
            List<Object> objects = communityRoleConnectMapper.selectObjs(communityRoleConnectQueryWrapper);
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("id", objects);
            userQueryWrapper.select("photo", "username", "id");
            List<User> userList = userMapper.selectList(userQueryWrapper);
            communityRole.setUserList(userList);
            communityRoleList.add(communityRole);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sum", sum);
        jsonObject.put("communityRoleList", communityRoleList);
        return R.ok(jsonObject);
    }

    /**
     * 通过社区id查询社区频道列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/7 17:15
     */
    @Override
    public R queryCommunityChannelListByCommunityId(Long communityId) {
        QueryWrapper<CommunityChannel> communityChannelQueryWrapper = new QueryWrapper<>();
        communityChannelQueryWrapper.eq("community_id", communityId);
        communityChannelQueryWrapper.orderByAsc("sort");
        communityChannelQueryWrapper.select("id", "channel_name");
        return R.ok(communityChannelMapper.selectList(communityChannelQueryWrapper));
    }

    /**
     * 发布社区文章
     *
     * @param userId 当前登录用户id
     * @param communityId 社区id
     * @param communityArticleVo 发布社区文章实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/8 10:07
     */
    @Override
    @Transactional
    public R publishArticle(Long userId, Long communityId, CommunityArticleVo communityArticleVo) {
        // 当前时间
        Date nowDate = new Date();

        // 插入社区文章表
        CommunityArticle communityArticle = new CommunityArticle();
        BeanUtils.copyProperties(communityArticleVo, communityArticle);

        communityArticle.setCommunityId(communityId);
        communityArticle.setUserId(userId);
        communityArticle.setChannelId(communityArticleVo.getChannelId());
        communityArticle.setPicture(communityArticleVo.getPicture());
        communityArticle.setIsTask(communityArticleVo.getIsTask());
        communityArticle.setIsVote(communityArticleVo.getIsVote());
        communityArticle.setStatus(CommunityEnum.REVIEW_PROGRESS.getCode());
        communityArticle.setCreateTime(nowDate);
        communityArticle.setUpdateTime(nowDate);

        communityArticleMapper.insert(communityArticle);
        Long articleId = communityArticle.getId();

        // 插入文章任务表
        if (communityArticle.getIsTask().equals(CommunityEnum.IS_TASK.getCode())) {
            CommunityArticleTask communityArticleTask = communityArticleVo.getCommunityArticleTask();

            communityArticleTask.setCommunityArticleId(articleId);
            communityArticleTask.setCreateTime(nowDate);
            communityArticleTask.setUpdateTime(nowDate);
            List<User> communityMemberList = communityArticleVo.getCommunityMemberList();
            if (communityMemberList != null) {
                String userIds = "";
                int communityMemberLen = communityMemberList.size();
                for (int i = 0; i < communityMemberLen; i ++ ) {
                    if (i == communityMemberLen - 1) {
                        userIds += communityMemberList.get(i).getId();
                    } else {
                        userIds += communityMemberList.get(i).getId() + ",";
                    }
                }
                communityArticleTask.setUserIds(userIds);
            }

            communityArticleTaskMapper.insert(communityArticleTask);
        }


        // 插入文章投票表
        if (communityArticle.getIsVote().equals(CommunityEnum.IS_VOTE.getCode())) {
            CommunityArticleVeto communityArticleVeto = communityArticleVo.getCommunityArticleVeto();
            communityArticleVeto.setCommunityArticleId(articleId);
            List<CommunityArticleVetoItem> communityArticleVetoItemList = communityArticleVeto.getCommunityArticleVetoItemList();
            int communityArticleVetoLen = communityArticleVetoItemList.size();
            communityArticleVeto.setVetoPeople(communityArticleVetoLen);
            communityArticleVeto.setUpdateTime(nowDate);
            communityArticleVeto.setCreateTime(nowDate);
            communityArticleVetoMapper.insert(communityArticleVeto);
            Long communityArticleVetoId = communityArticleVeto.getId();

            for (CommunityArticleVetoItem communityArticleVetoItem : communityArticleVetoItemList) {
                communityArticleVetoItem.setCommunityArticleVetoId(communityArticleVetoId);
                communityArticleVetoItem.setCreateTime(new Date());
                communityArticleVetoItemMapper.insert(communityArticleVetoItem);
            }
        }

        return R.ok();
    }
}

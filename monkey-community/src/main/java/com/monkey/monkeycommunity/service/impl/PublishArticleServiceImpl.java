package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.mapper.CommunityRoleConnectMapper;
import com.monkey.monkeycommunity.mapper.CommunityRoleMapper;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import com.monkey.monkeycommunity.pojo.CommunityRoleConnect;
import com.monkey.monkeycommunity.service.PublishArticleService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        return null;
    }
}

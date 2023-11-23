package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommunityManageMenuEnum;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuConnectMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuMapper;
import com.monkey.monkeyUtils.pojo.CommunityManageMenu;
import com.monkey.monkeyUtils.pojo.CommunityManageMenuConnect;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.AuthorityMethods;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeyUtils.mapper.CommunityManageMapper;
import com.monkey.monkeyUtils.pojo.CommunityManage;
import com.monkey.monkeycommunity.mapper.CommunityUserRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import com.monkey.monkeycommunity.service.manage.AdminConfigService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeycommunity.util.CommonMethod;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.util.JAXBSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/6 17:00
 * @version: 1.0
 * @description:
 */
@Service
public class AdminConfigServiceImpl implements AdminConfigService {
    @Resource
    private CommunityManageMapper communityManageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityManageMenuMapper communityManageMenuMapper;
    @Resource
    private CommunityManageMenuConnectMapper communityManageMenuConnectMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    /**
     * 查询社区管理列表
     *
     * @param communityId 社区id
     * @param manageIdx 管理员编号
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 17:07
     */
    @Override
    public R queryCommunityManager(Long communityId, Long currentPage, Integer pageSize, String manageIdx) {
        QueryWrapper<CommunityManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("community_id", communityId);
        communityUserManageQueryWrapper.like(manageIdx != null && !"".equals(manageIdx), "user_id", manageIdx);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityManageMapper.selectPage(page, communityUserManageQueryWrapper);
        List<CommunityManage> communityManageList = selectPage.getRecords();
        for (CommunityManage communityManage : communityManageList) {
            Long userId = communityManage.getUserId();
            User user = userMapper.selectById(userId);
            communityManage.setUsername(user.getUsername());
            communityManage.setHeadImg(user.getPhoto());
        }

        selectPage.setRecords(communityManageList);
        return R.ok(selectPage);
    }

    /**
     * 删除管理员
     *
     * @param communityManageId 社区管理员id 
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 17:30
     */
    @Override
    @Transactional
    public R deleteManager(Long communityManageId) {
        CommunityManage communityManage = communityManageMapper.selectById(communityManageId);
        Integer isPrime = communityManage.getIsPrime();
        if (isPrime.equals(CommunityEnum.IS_PRIME_MANAGE.getCode())) {
            return R.error(TipConstant.primeAdminNotDelete);
        }


        if (communityManage.getUserId().equals(Long.parseLong(JwtUtil.getUserId()))) {
            return R.error(TipConstant.notOperatorOfMy);
        }

        // 删除管理员权限表
        LambdaQueryWrapper<CommunityManageMenuConnect> communityManageMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityManageMenuConnectLambdaQueryWrapper.eq(CommunityManageMenuConnect::getCommunityManageId, communityManageId);
        communityManageMenuConnectLambdaQueryWrapper.select(CommunityManageMenuConnect::getCommunityManageMenuId);
        List<Object> communityManageMenuIdList = communityManageMenuConnectMapper
                .selectObjs(communityManageMenuConnectLambdaQueryWrapper);

        if (communityManageMenuIdList != null && communityManageMenuIdList.size() > 0) {
            LambdaQueryWrapper<CommunityManageMenu> communityManageMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            communityManageMenuLambdaQueryWrapper.in(CommunityManageMenu::getId, communityManageMenuIdList);
            communityManageMenuMapper.delete(communityManageMenuLambdaQueryWrapper);
            // 删除社区管理权限关联表
            communityManageMenuConnectMapper.delete(communityManageMenuConnectLambdaQueryWrapper);
        }

        // 删除社区管理员关联表
        communityManageMapper.deleteById(communityManageId);

        List<String> perms = new ArrayList<>();
        Long communityId = communityManage.getCommunityId();
        perms.add(CommunityManageMenuEnum.USER_MANAGE.getPerms() + communityId);
        perms.add(CommunityManageMenuEnum.ROLE_MANAGE.getPerms() + communityId);
        perms.add(CommunityManageMenuEnum.USER_APPLICATION.getPerms() + communityId);
        perms.add(CommunityManageMenuEnum.CHANNEL_MANAGE.getPerms() + communityId);
        perms.add(CommunityManageMenuEnum.CONTENT_MANAGE.getPerms() + communityId);
        perms.add(CommunityManageMenuEnum.INFORMATION_MANAGE.getPerms() + communityId);
        AuthorityMethods.batchDeleteUserAuthority(String.valueOf(communityManage.getUserId()), perms, stringRedisTemplate);
        return R.ok();
    }

    /**
     * 新增管理员
     *
     * @param communityId 社区id
     * @param userId 新增管理员id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 17:31
     */
    @Override
    @Transactional
    public R addManager(Long communityId, String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(TipConstant.noExistUser);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        Long selectCount = userMapper.selectCount(userQueryWrapper);
        if (selectCount <= 0) {
            return R.error(TipConstant.noExistUser);
        }

        // 判断该用户是否为社区成员
        LambdaQueryWrapper<CommunityUserRoleConnect> communityUserRoleConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityUserRoleConnectLambdaQueryWrapper.eq(CommunityUserRoleConnect::getCommunityId, communityId);
        communityUserRoleConnectLambdaQueryWrapper.eq(CommunityUserRoleConnect::getUserId, userId);
        Long roleCount = communityUserRoleConnectMapper.selectCount(communityUserRoleConnectLambdaQueryWrapper);
        if (roleCount <= 0) {
            return R.error(TipConstant.managerNecessityInCommunity);
        }
        // 判断是否已经添加过
        QueryWrapper<CommunityManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("user_id", userId);
        communityUserManageQueryWrapper.eq("community_id", communityId);
        Long count = communityManageMapper.selectCount(communityUserManageQueryWrapper);
        if (count > 0) {
            return R.error(TipConstant.existManage);
        }

        CommunityManage communityManage = new CommunityManage();
        communityManage.setCommunityId(communityId);
        communityManage.setUserId(Long.parseLong(userId));
        communityManage.setCreateTime(new Date());
        communityManage.setCreateUser(Long.parseLong(userId));
        communityManageMapper.insert(communityManage);
        Long communityManageId = communityManage.getId();

        // 加入管理员权限
        // 插入用户管理权限表
        CommonMethod.insertCommunityManagerMenu(communityManageId,
                communityId,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);
        return R.ok();
    }
}

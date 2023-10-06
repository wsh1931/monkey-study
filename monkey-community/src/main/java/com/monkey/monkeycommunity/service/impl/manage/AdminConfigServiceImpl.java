package com.monkey.monkeycommunity.service.impl.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.mapper.CommunityUserManageMapper;
import com.monkey.monkeycommunity.pojo.CommunityUserManage;
import com.monkey.monkeycommunity.service.manage.AdminConfigService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
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
    private CommunityUserManageMapper communityUserManageMapper;
    @Resource
    private UserMapper userMapper;

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
        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("community_id", communityId);
        communityUserManageQueryWrapper.like(manageIdx != null && !"".equals(manageIdx), "user_id", manageIdx);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserManageMapper.selectPage(page, communityUserManageQueryWrapper);
        List<CommunityUserManage> communityUserManageList = selectPage.getRecords();
        for (CommunityUserManage communityUserManage : communityUserManageList) {
            Long userId = communityUserManage.getUserId();
            User user = userMapper.selectById(userId);
            communityUserManage.setUsername(user.getUsername());
            communityUserManage.setHeadImg(user.getPhoto());
        }

        selectPage.setRecords(communityUserManageList);
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
    public R deleteManager(Long communityManageId) {
        int deleteById = communityUserManageMapper.deleteById(communityManageId);
        if (deleteById > 0) {
            return R.ok();
        }
        return R.error(TipConstant.noExistAgain);
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

        // 判断是否已经添加过
        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("user_id", userId);
        communityUserManageQueryWrapper.eq("community_id", communityId);
        Long count = communityUserManageMapper.selectCount(communityUserManageQueryWrapper);
        if (count > 0) {
            return R.error(TipConstant.existManage);
        }

        CommunityUserManage communityUserManage = new CommunityUserManage();
        communityUserManage.setCommunityId(communityId);
        communityUserManage.setUserId(Long.parseLong(userId));
        communityUserManage.setCreateTime(new Date());
        communityUserManageMapper.insert(communityUserManage);
        return R.ok();
    }
}

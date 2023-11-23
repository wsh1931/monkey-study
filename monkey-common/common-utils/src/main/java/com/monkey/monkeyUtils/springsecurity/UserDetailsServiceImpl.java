package com.monkey.monkeyUtils.springsecurity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.CommunityManageMenuEnum;
import com.monkey.monkeyUtils.constants.CommunityMenuEnum;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CommunityManageMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuConnectMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuMapper;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.CommunityManage;
import com.monkey.monkeyUtils.pojo.CommunityManageMenu;
import com.monkey.monkeyUtils.pojo.CommunityManageMenuConnect;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CommunityManageMapper communityManageMapper;
    @Resource
    private CommunityManageMenuConnectMapper communityManageMenuConnectMapper;
    @Resource
    private CommunityManageMenuMapper communityManageMenuMapper;


    /**
     * 查询用户信息
     * 查询授权信息
     *
     * @param username 用户名
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/19 16:21
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(userLambdaQueryWrapper);

        if (user == null) {
            throw new MonkeyBlogException(ExceptionEnum.USER_NOT_EXIST.getCode(), ExceptionEnum.USER_NOT_EXIST.getMsg());
        }
        // 得到社区管理用户授权信息
        Long userId = user.getId();
        List<String> communityUserMenu = this.getCommunityUserAuthority(userId);

        return new UserDetailsImpl(user, communityUserMenu);
    }

    /**
     * 得到社区管理用户授权信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 9:37
     */
    private List<String> getCommunityUserAuthority(Long userId) {
        // 查找用户管理集合
        LambdaQueryWrapper<CommunityManage> communityManageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityManageLambdaQueryWrapper.eq(CommunityManage::getUserId, userId);
        communityManageLambdaQueryWrapper.eq(CommunityManage::getStatus, CommunityMenuEnum.STATUS_NORMAL.getCode());
        communityManageLambdaQueryWrapper.select(CommunityManage::getId, CommunityManage::getManageKey);
        List<CommunityManage> communityManageList = communityManageMapper.selectList(communityManageLambdaQueryWrapper);

        if (communityManageList == null || communityManageList.size() <= 0) {
            return new ArrayList<>();
        }
        // 查找社区用户管理权限集合
        List<Long> communityManageIdList = communityManageList.stream().mapToLong(CommunityManage::getCommunityId).boxed().collect(Collectors.toList());
        LambdaQueryWrapper<CommunityManageMenuConnect> communityManageMenuConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityManageMenuConnectLambdaQueryWrapper.in(CommunityManageMenuConnect::getCommunityManageId, communityManageIdList);
        communityManageMenuConnectLambdaQueryWrapper.select(CommunityManageMenuConnect::getCommunityManageMenuId);
        List<Object> communityManageMenuIdList = communityManageMenuConnectMapper.selectObjs(communityManageMenuConnectLambdaQueryWrapper);

        LambdaQueryWrapper<CommunityManageMenu> communityManageMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityManageMenuLambdaQueryWrapper.in(CommunityManageMenu::getId, communityManageMenuIdList);
        communityManageMenuLambdaQueryWrapper.select(CommunityManageMenu::getPerms);
        List<CommunityManageMenu> communityManageMenuList = communityManageMenuMapper.selectList(communityManageMenuLambdaQueryWrapper);

        List<String> permsList = communityManageMenuList
                .stream()
                .map(CommunityManageMenu::getPerms)
                .distinct()
                .collect(Collectors.toList());

        communityManageList.stream().forEach(f -> permsList.add(f.getManageKey()));
        return permsList;
    }
}
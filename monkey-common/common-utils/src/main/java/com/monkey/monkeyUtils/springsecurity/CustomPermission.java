package com.monkey.monkeyUtils.springsecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/21 16:51
 * @version: 1.0
 * @description: 自定义springsecurity权限
 */
@Component
public class CustomPermission {
    public boolean hasCommunityManagerAuthority(String authority) {
        // 获取当前用户的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> permissions = userDetails.getPermissions();
        // 判断用户权限集合中是否存在authority
        return permissions.contains(authority);
    }

}

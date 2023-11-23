package com.monkey.monkeycommunity.authority;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/22 14:49
 * @version: 1.0
 * @description: 社区自定义权限
 */
@Component
public class CommunityCustomAuthority {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 判断用户是否具有社区管理的权限
     *
     * @param perms
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 16:01
     */
    public boolean communityManageAuthority(String perms) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        List<String> permissions = userDetails.getPermissions();
        if (permissions == null) {
            return false;
        }
        if (permissions.contains(perms)) {
            return true;
        }
        return false;
    }
}

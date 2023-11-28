package com.monkey.monkeycommunity.authority;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
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
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    /**
     * 判断用户是否具有社区管理的权限
     *
     * @param perms 权限集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 16:01
     */
    public boolean communityManageAuthority(String ...perms) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        List<String> permissions = userDetails.getPermissions();
        if (permissions == null) {
            return false;
        }

        // 查询是否存在传入的权限
        return Arrays.stream(perms).anyMatch(permissions::contains);
    }

    /**
     * 判断当前登录用户是否是文章作者
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/27 16:18
     */
    public boolean judgeIsCommunityArticleAuthor(Long communityArticleId) {
        long nowUserId = Long.parseLong(JwtUtil.getUserId());
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        if (!communityArticle.getUserId().equals(nowUserId)) {
            return false;
        }

        return true;
    }
}

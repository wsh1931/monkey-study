package com.monkey.monkeyUtils.springsecurity;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 从哪里读取token
        String token = request.getHeader("Authorization");
        // token以Bearer开头
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            // 若不存在token, 直接放行，不将用户信息存入getContext中
            // 让后续过滤器判断该用户是否是认证状态
            filterChain.doFilter(request, response);
            // 加上return 是防止后面过滤器解析完之后再次执行解析token的操作
            return;
        }
        token = token.substring(7);

        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new MonkeyBlogException(ExceptionEnum.TOKEN_ERROR.getCode(), ExceptionEnum.TOKEN_ERROR.getMsg());
        }

        // 解析完token后得到用户信息
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        String userStr = stringRedisTemplate.opsForValue().get(redisKey);
        UserDetailsImpl userDetails  = JSONObject.parseObject(userStr, UserDetailsImpl.class);
        if (userDetails == null) {
            throw new MonkeyBlogException(ExceptionEnum.USER_NOT_EXIST.getCode(), ExceptionEnum.USER_NOT_EXIST.getMsg());
        }

        // 获取权限信息封装到authenticationToken中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 因为后面的过滤器都要从此方法中获得用户信息，所以要将用户信息存在此处
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
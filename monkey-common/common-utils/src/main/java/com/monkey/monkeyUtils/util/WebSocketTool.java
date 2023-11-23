package com.monkey.monkeyUtils.util;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.jsonwebtoken.Claims;

public class WebSocketTool {
    // 通过token得到当前用户id
    public static Long getUserIdBytoken(String token) {
        Long userId = -1L;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userId;
    }
}

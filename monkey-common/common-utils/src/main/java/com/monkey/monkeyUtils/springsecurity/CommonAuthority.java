package com.monkey.monkeyUtils.springsecurity;

import org.springframework.stereotype.Component;

/**
 * @author: wusihao
 * @date: 2023/11/23 21:11
 * @version: 1.0
 * @description:
 */
@Component
public class CommonAuthority {
    /**
     * 所有用户都可以通过
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/23 21:11
     */
    public boolean allUser() {
        return true;
    }

    /**
     * 判断当前用户是否与目标用户id一样
     *
     * @param userId 目标用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 14:48
     */
    public boolean isSameUser(Long userId) {
        Long nowUserId = Long.parseLong(JwtUtil.getUserId());
        return nowUserId.equals(userId);
    }
}

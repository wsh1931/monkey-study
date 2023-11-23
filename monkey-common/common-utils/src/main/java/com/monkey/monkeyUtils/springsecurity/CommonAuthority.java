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
}

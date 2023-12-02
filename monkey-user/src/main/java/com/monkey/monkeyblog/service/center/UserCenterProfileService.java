package com.monkey.monkeyblog.service.center;

import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.result.R;

public interface UserCenterProfileService {
    // 查询用户信息
    R queryUserInfo();

    // 更新用户头像
    R updateUserHeadImg(String photo);

    // 更新用户信息
    R updateUserInfo(User user);
}

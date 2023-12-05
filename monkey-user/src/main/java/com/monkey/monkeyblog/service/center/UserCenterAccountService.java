package com.monkey.monkeyblog.service.center;

import com.monkey.monkeyUtils.result.R;

public interface UserCenterAccountService {
    // 修改密码
    R modifyPassword(String password, String confirmPassword);

    // 得到用户信息
    R queryUserInfo();

    // 得到用户邮箱
    R queryUserEmail();

    // 发送邮箱验证码
    R sendEmailVerify();

    // 提交验证码
    R submitVerify(String verifyCode);

    // 判断用户邮箱是否被验证
    R judgeUserEmailIsVerify();

    // 判断用户邮箱是否绑定
    R judgeUserEmailIsBind();

    // 发送邮箱绑定验证码
    R sendEmailBindVerify(String email);

    // 提交绑定邮箱验证码
    R submitBindVerify(String verifyCode, String email);
}

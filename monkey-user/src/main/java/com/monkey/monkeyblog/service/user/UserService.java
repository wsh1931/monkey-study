package com.monkey.monkeyblog.service.user;


import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ResultVO userRegister(RegisterVo registerVo);
    ResultVO loginUsername(String username, String password, String verifyCode);

    ResultVO getUserInfoByToken();

    // 发送验证码给对方QQ邮箱
    ResultVO sendVerfyCode(String targetEmail, String isRegister);

    // 生成验证码
    void getCaptcha(HttpServletResponse response);

    // 通过邮箱验证码登录
    ResultVO loginEmail(String email, String verifyCode);


}

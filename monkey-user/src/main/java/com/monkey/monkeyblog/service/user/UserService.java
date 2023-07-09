package com.monkey.monkeyblog.service.user;


import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ResultVO userRegister(RegisterVo registerVo);
    ResultVO userLogin(String username, String password);

    ResultVO getUserInfoByToken();

    // 发送验证码给对方QQ邮箱
    ResultVO sendVerfyCode(String targetEmail);

    // 生成验证码
    void getCaptcha(HttpServletResponse response);
}

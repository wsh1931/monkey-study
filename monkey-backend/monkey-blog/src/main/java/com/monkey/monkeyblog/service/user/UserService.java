package com.monkey.monkeyblog.service.user;


import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface UserService {
    ResultVO userRegister(Map<String, String> userInfo);
    ResultVO userLogin(Map<String, String> userInfo);

    ResultVO getUserInfoByToken();
}

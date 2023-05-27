package com.monkey.monkeybackend.Service.User;

import com.monkey.monkeybackend.utils.result.ResultVO;

import java.util.Map;

public interface UserService {
    ResultVO userRegister(Map<String, String> userInfo);
    ResultVO userLogin(Map<String, String> userInfo);

    ResultVO getUserInfoByToken();
}

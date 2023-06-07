package com.monkey.monkeyblog.service.user.center;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface UserHomeService {

    // 通过用户id查询用户信息
    ResultVO getUserInformationByUserId(Map<String, String> data);
}

package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface WebSocketChatService {
//    通过当前登录用户登录id得到该用户聊天列表
    ResultVO getReplyUserListByUserId(Map<String, String> data);

    // 得到聊天对话框信息
    ResultVO showChatInformation(Map<String, String> data);

    // 通过用户名模糊查找用户信息中的用户名
    ResultVO getUserListByUsername(Map<String, String> data);
}

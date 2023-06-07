package com.monkey.monkeyblog.service.chat;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.UserChatVo;

import java.util.List;
import java.util.Map;

public interface WebSocketChatService {
//    通过当前登录用户登录id得到该用户聊天列表
    ResultVO getReplyUserListByUserId(Map<String, String> data);

    // 得到聊天对话框信息
    ResultVO showChatInformation(Map<String, String> data);

    // 通过用户名模糊查找用户信息中的用户名
    ResultVO getUserListByUsername(Map<String, String> data);
}

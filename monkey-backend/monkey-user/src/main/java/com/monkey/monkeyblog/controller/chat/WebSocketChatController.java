package com.monkey.monkeyblog.controller.chat;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.chat.WebSocketChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/webSocketChat")
public class WebSocketChatController {
    @Autowired
    private WebSocketChatService webSocketChatService;

    // 通过当前登录用户登录id得到该用户聊天列表
    @GetMapping("/getReplyUserListByUserId")
    public ResultVO getReplyUserListByUserId(@RequestParam Map<String, String> data) {
        return webSocketChatService.getReplyUserListByUserId(data);
    }

    // 得到聊天对话框信息
    @GetMapping("/showChatInformation")
    public ResultVO showChatInformation(@RequestParam Map<String, String> data) {
        return webSocketChatService.showChatInformation(data);
    }

    // 通过用户名模糊查找用户信息中的用户名
    @GetMapping("/getUserListByUsername")
    public ResultVO getUserListByUsername(@RequestParam Map<String, String> data) {
        return webSocketChatService.getUserListByUsername(data);
    }
}

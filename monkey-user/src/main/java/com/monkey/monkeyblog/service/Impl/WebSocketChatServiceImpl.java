package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.mapper.ChatHistoryMapper;
import com.monkey.monkeyblog.pojo.ChatHistory;
import com.monkey.monkeyblog.pojo.Vo.UserChatVo;
import com.monkey.monkeyblog.service.UserFeignService;
import com.monkey.monkeyblog.service.WebSocketChatService;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebSocketChatServiceImpl implements WebSocketChatService {

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFeignService userFeignService;

    //通过当前登录用户登录id得到该用户聊天信息列表（左边）
    @Override
    public ResultVO getReplyUserListByUserId(Map<String, String> data) {
        String userId = JwtUtil.getUserId();
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper = new QueryWrapper<>();
        chatHistoryQueryWrapper.eq("sender_id", userId).or().eq("receiver_id", userId);
        chatHistoryQueryWrapper.orderByDesc("create_time");
        List<ChatHistory> chatHistoryList = chatHistoryMapper.selectList(chatHistoryQueryWrapper);
        // 1: 将sender_id, receiver_id交换排序去重后再按时间降序取出最晚消息发出的时间
        // 1.1 将 sender_id, receiver_id按sender_id在前，receiver_id在后
        Map<Long, Boolean> isSwap = new HashMap<>(); // 判断(sender_id, receiver_id)是否交换过
        Long len = Long.parseLong(String.valueOf(chatHistoryList.size()));
        for (long i = 0; i < len; i ++ ) {
            ChatHistory chatHistory = chatHistoryList.get((int)i);
            Long senderId = chatHistory.getSenderId();
            Long receiverId = chatHistory.getReceiverId();
            if (senderId > receiverId) {
                chatHistory.setSenderId(receiverId);
                chatHistory.setReceiverId(senderId);
                chatHistoryList.set((int) i, chatHistory);
                isSwap.put(i, true);
            }
        }

        // 找出时间最晚的一条记录，将多余的数据去除.
        List<ChatHistory> chatHistories = new ArrayList<>();
        Map<Map.Entry<Long, Long>, Boolean> resList = new HashMap<>();
        for (int i = 0; i < len; i ++ ) {
            ChatHistory chatHistory = chatHistoryList.get(i);
            Long receiverId = chatHistory.getReceiverId();
            Long senderId = chatHistory.getSenderId();
            Map.Entry<Long, Long> key1 = new AbstractMap.SimpleEntry<>(senderId, receiverId);
            if (resList.get(key1) == null || !resList.get(key1)) {
                // 判断之前是否交换过sender_id, receiver_id
                if (isSwap.get(i) != null && isSwap.get(i)) {
                    chatHistory.setSenderId(receiverId);
                    chatHistory.setReceiverId(senderId);
                }
                chatHistories.add(chatHistory);
                resList.put(key1, true);
            }
        }

        List<UserChatVo> userChatVoList = new ArrayList<>();

        // 若当前用户没有向该作者发送过消息，则在数据库填入一个空字段以便在列表左边显示
        long statrReceiverId = Long.parseLong(data.get("receiverId"));
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper1 = new QueryWrapper<>();
        chatHistoryQueryWrapper1.eq("sender_id", userId).eq("receiver_id", statrReceiverId)
                .or().eq("receiver_id", userId).eq("sender_id", statrReceiverId);
        Long count = chatHistoryMapper.selectCount(chatHistoryQueryWrapper1);
        if (count <= 0) {
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setSenderId(Long.parseLong(userId));
            chatHistory.setReceiverId(statrReceiverId);
            chatHistory.setCreateTime(new Date());
            chatHistory.setContent("快开始聊天吧。");
            chatHistoryMapper.insert(chatHistory);
            ChatHistory chatHistory1 = chatHistoryMapper.selectById(chatHistory.getId());
            UserChatVo userChatVo = new UserChatVo();
            Long senderId = chatHistory1.getSenderId();
            Long receiverId = chatHistory1.getReceiverId();

            userChatVo.setId(chatHistory1.getId());
            userChatVo.setLastCreateTime(chatHistory1.getCreateTime());
            userChatVo.setLastContent(chatHistory1.getContent());
            // 通过发送者id得到发送者信息
            User userSender = userMapper.selectById(senderId);

            userChatVo.setSenderId(senderId);
            userChatVo.setSenderName(userSender.getUsername());
            userChatVo.setSenderPhoto(userSender.getPhoto());
            userChatVo.setSenderBrief(userSender.getBrief());
            // 通过接收者id得到接收者信息
            User userReceiver = userMapper.selectById(receiverId);
            userChatVo.setReceiverName(userReceiver.getUsername());
            userChatVo.setReceiverPhoto(userReceiver.getPhoto());
            userChatVo.setReceiverBrief(userReceiver.getBrief());
            userChatVo.setReceiverId(userReceiver.getId());

            Long selectCount = (long)userFeignService.judgeLoginUserAndAuthorConnect(senderId, receiverId).getData();
            userChatVo.setIsLike(selectCount);
            userChatVoList.add(userChatVo);
        }

        for (int i = 0; i < chatHistories.size(); i ++ ) {
            UserChatVo userChatVo = new UserChatVo();
            ChatHistory chatHistory = chatHistories.get(i);
            Long senderId = chatHistory.getSenderId();
            Long receiverId = chatHistory.getReceiverId();

            userChatVo.setId(chatHistory.getId());
            userChatVo.setLastCreateTime(chatHistory.getCreateTime());
            userChatVo.setLastContent(chatHistory.getContent());
            // 通过发送者id得到发送者信息

            User userSender = userMapper.selectById(senderId);
            userChatVo.setSenderId(senderId);
            userChatVo.setSenderName(userSender.getUsername());
            userChatVo.setSenderPhoto(userSender.getPhoto());
            userChatVo.setSenderBrief(userSender.getBrief());
            // 通过接收者id得到接收者信息
            User userReceiver = userMapper.selectById(receiverId);
            userChatVo.setReceiverName(userReceiver.getUsername());
            userChatVo.setReceiverPhoto(userReceiver.getPhoto());
            userChatVo.setReceiverBrief(userReceiver.getBrief());
            userChatVo.setReceiverId(userReceiver.getId());

            Long selectCount = (long) userFeignService.judgeLoginUserAndAuthorConnect(senderId, receiverId).getData();
            userChatVo.setIsLike(selectCount);
            userChatVoList.add(userChatVo);
        }
        return new ResultVO(ResultStatus.OK, null, userChatVoList);
    }

    // 得到聊天对话框信息（右边）
    @Override
    public ResultVO showChatInformation(Map<String, String> data) {
        long senderId = Long.parseLong(data.get("senderId"));
        long receiverId = Long.parseLong(data.get("receiverId"));
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper = new QueryWrapper<>();
        chatHistoryQueryWrapper.orderByAsc("create_time");
        // 得到双方聊天记录
        chatHistoryQueryWrapper.eq("sender_id", senderId).eq("receiver_id", receiverId)
                .or().eq("receiver_id", senderId).eq("sender_id", receiverId);
        List<ChatHistory> chatHistoryList = chatHistoryMapper.selectList(chatHistoryQueryWrapper);
        List<UserChatVo> userChatVoList = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistoryList) {
            UserChatVo userChatVo = new UserChatVo();
            userChatVo.setId(chatHistory.getId());
            // 通过senderId, receiverId得到对应信息
            User userSender = userMapper.selectById(senderId);
            userChatVo.setSenderBrief(userSender.getBrief());
            userChatVo.setSenderPhoto(userSender.getPhoto());
            userChatVo.setSenderId(userSender.getId());
            userChatVo.setSenderName(userSender.getUsername());

            User userReceiver = userMapper.selectById(receiverId);

            userChatVo.setReceiverId(userReceiver.getId());
            userChatVo.setReceiverName(userReceiver.getUsername());
            userChatVo.setReceiverBrief(userReceiver.getBrief());
            userChatVo.setReceiverPhoto(userReceiver.getPhoto());
            userChatVo.setContent(chatHistory.getContent());
            userChatVo.setCreateTime(chatHistory.getCreateTime());
            userChatVoList.add(userChatVo);
        }
        // 因为websocket一次发送的消息有允许发送的消息有大小限制所以每次确定最多发送十条消息

        if (userChatVoList.size() < 10) {
            return new ResultVO(ResultStatus.OK, null, userChatVoList);
        } else {
            List<UserChatVo> res = new ArrayList<>();
            for (int i = userChatVoList.size() - 10; i < userChatVoList.size(); i ++ ) {
                res.add(userChatVoList.get(i));
            }
            return new ResultVO(ResultStatus.OK, null, res);
        }
    }

    // 通过用户名模糊查找用户信息中的用户名
    @Override
    public ResultVO getUserListByUsername(Map<String, String> data) {
        List<UserChatVo> userChatVo = JSON.parseArray(data.get("userChatVo"), UserChatVo.class);
        String username = data.get("username");
        List<UserChatVo> resList = new ArrayList<>();
        // 模糊查询用户名
        for (UserChatVo user : userChatVo) {
            String receiverName = user.getReceiverName();
            if (receiverName.contains(username)) {
                resList.add(user);
            }
        }
        return new ResultVO(ResultStatus.OK, null, resList);
    }
}

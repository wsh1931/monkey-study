package com.monkey.monkeyblog.websocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyblog.mapper.ChatHistoryMapper;
import com.monkey.monkeyblog.pojo.ChatHistory;
import com.monkey.monkeyblog.pojo.Vo.UserChatVo;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.util.WebSocketTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/chat/{token}")  // 注意不要以'/'结尾
public class WebSocketChatServer {

    // 将用户id 映射到每个websocket实例，以便通过用户id找到其对应的websocket
    // static 所有实例访问同一个哈希表
    public static ConcurrentHashMap<Long, WebSocketChatServer> userList = new ConcurrentHashMap<>();

    // 用户从后端向前端发送消息
    private Session session = null;

    private User user;

    private static UserMapper userMapper;

    private static ChatHistoryMapper chatHistoryMapper;

    @Autowired
    public void setChatHistoryMapper(ChatHistoryMapper chatHistoryMapper) {
        WebSocketChatServer.chatHistoryMapper = chatHistoryMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketChatServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        System.err.println("开始聊天");
        // 建立连接
        this.session = session;
        Long senderId = WebSocketTool.getUserIdBytoken(token);
        this.user = userMapper.selectById(senderId);
        log.info("user = {}", this.user);
        if (this.user != null) {
            userList.put(senderId, this);
        } else {
            try {
                this.session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(message);
        // 从Client接收消息
        JSONObject data = JSONObject.parseObject(message);
        String event = (String)data.get("event");
        if ("start_chat".equals(event)) {
            JSONArray messageArray = JSONArray.parseArray(data.getString("message"));
            List<UserChatVo> userChatVoList = this.getListByJSON(messageArray, UserChatVo.class);
            // 判断聊天者方向
            List<UserChatVo> chatVoList = this.judgeDirection(userChatVoList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", event);
            jsonObject.put("messageList", chatVoList);
            this.sendMessage(jsonObject.toJSONString());
        } else if ("send_message".equals(event)) {
            String dataString = data.getString("message");
            Long receiverId = data.getLong("receiverId");
            // 向发送者和接收者传递消息
            this.sendMessageDeleiverMessage(dataString, receiverId);
        }
//        } else if ("user_like_article".equals(event)) { // 用户点赞文章
//            log.error("data = {}", data);
//            Long articleId = data.getLong("articleId");
//            userLikeArticle(articleId);
//        }
    }

//    private void userLikeArticle(Long articleId) {
//        Article article = articleM apper.selectById(articleId);
//        // replyId为被点赞用户信息
//        Long replyId = article.getUserId();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("event", "user_receiver_like_information");
//        jsonObject.put("like_user", this.user.getId());
//        WebSocketChatServer webSocketChatServer = this.userList.get(replyId);
//        if (webSocketChatServer == null) {
//            // todo 将消息放入消息队列中
//        } else {
//            webSocketChatServer.sendMessage(jsonObject.toJSONString(jsonObject));
//        }
//    }

    // 通过接收者id发送消息给接收者
    private void sendMessageDeleiverMessage(String dataString, Long receiverId) {
        // 将该消息加入数据库
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setContent(dataString);
        chatHistory.setCreateTime(new Date());
        chatHistory.setReceiverId(receiverId);
        chatHistory.setSenderId(user.getId());
        chatHistoryMapper.insert(chatHistory);
        ChatHistory history = chatHistoryMapper.selectById(chatHistory.getId());

        // 将消息返回前端
        JSONObject jsonObjectReceiver = new JSONObject();
        jsonObjectReceiver.put("event", "receive_message");
        User receiver = userMapper.selectById(receiverId);
        UserChatVo userChatVoReceiver = new UserChatVo();
        userChatVoReceiver.setContent(dataString);
        userChatVoReceiver.setCreateTime(history.getCreateTime());
        userChatVoReceiver.setDirection("左");
        userChatVoReceiver.setReceiverName(receiver.getUsername());
        userChatVoReceiver.setReceiverPhoto(receiver.getPhoto());
        jsonObjectReceiver.put("information", userChatVoReceiver);
        WebSocketChatServer webSocketChatServer = userList.get(receiverId);
        if (webSocketChatServer != null) {
            webSocketChatServer.sendMessage(jsonObjectReceiver.toJSONString());
        }


        JSONObject jsonObjectSender = new JSONObject();
        jsonObjectSender.put("event", "send_message");
        UserChatVo userChatVoSender = new UserChatVo();
        userChatVoSender.setSenderName(this.user.getUsername());
        userChatVoSender.setSenderPhoto(this.user.getPhoto());
        userChatVoSender.setCreateTime(history.getCreateTime());
        userChatVoSender.setDirection("右");
        userChatVoSender.setContent(dataString);
        jsonObjectSender.put("information", userChatVoSender);
        userList.get(this.user.getId()).sendMessage(jsonObjectSender.toJSONString());
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        // 关闭连接时删除该用户
        if (this.user != null) {
            userList.remove(this.user.getId());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 后端像前端发送单个信息
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 将JSON集合转化成JAVA中的实体类
    public <T> List<T> getListByJSON(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            T object = JSONObject.toJavaObject(jsonObject, clazz);
            list.add(object);
        }
        return list;
    }

    private List<UserChatVo> judgeDirection(List<UserChatVo> userChatVoList) {
        for (UserChatVo userChat : userChatVoList) {
            Long senderId = userChat.getSenderId();
            Long receiverId = userChat.getReceiverId();
            if (senderId.equals(this.user.getId())) {
                userChat.setDirection("右");
            } else if (receiverId.equals(this.user.getId())) {
                userChat.setDirection("左");
            }
        }

        return userChatVoList;
    }
}
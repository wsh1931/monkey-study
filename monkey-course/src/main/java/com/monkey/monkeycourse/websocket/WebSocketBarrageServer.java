package com.monkey.monkeycourse.websocket;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeycourse.pojo.CourseVideoBarrage;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.util.WebSocketTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.monkey.monkeyUtils.util.DateSelfUtils.getSpecialFormatBySeconds;
import static com.monkey.monkeyUtils.util.DateUtils.format;
import static com.monkey.monkeyUtils.util.UniqueIdGenerator.generateUniqueId;

/**
 * @author: wusihao
 * @date: 2023/8/19 16:24
 * @version: 1.0
 * @description:
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/barrage/{token}")
public class WebSocketBarrageServer {

    public static ConcurrentHashMap<Long, WebSocketBarrageServer> userList = new ConcurrentHashMap<>();

    private Session session = null;

    // 判断用户是否为当前登录用户
    private User user;

    private static UserMapper userMapper;

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        WebSocketBarrageServer.rabbitTemplate = rabbitTemplate;
    }



    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketBarrageServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token")String token) {
        // 建立连接
        this.session = session;
        Long userId = WebSocketTool.getUserIdBytoken(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            userList.put(userId, this);
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
        JSONObject jsonObject = JSONObject.parseObject(message);
        String event = (String)jsonObject.get("event");
        log.info("接收到消息 => {}", event);
        if ("send_barrage".equals(event)) {
            // 接收发送的弹幕
            String currentTime = jsonObject.getString("currentTime");
            String barrageContent = jsonObject.getString("barrageContent");
            Long courseVideoId = jsonObject.getLong("courseVideoId");
            String barrageColor = jsonObject.getString("barrageColor");
            this.receiverBarrage(currentTime, barrageContent, courseVideoId, barrageColor);
        }

    }

    /**
     * 接收前端发送过来的弹幕信息
     *
     * @param currentTime 弹幕相对于当前视频的发送时间
     * @param barrageColor 弹幕颜色
     * @param courseVideoId 弹幕视频id
     * @param barrageContent 弹幕内容
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/20 11:51
     */
    private void receiverBarrage(String currentTime, String barrageContent, Long courseVideoId, String barrageColor) {
        CourseVideoBarrage courseVideoBarrage = new CourseVideoBarrage();
        courseVideoBarrage.setId(generateUniqueId());
        courseVideoBarrage.setBarrageColor(barrageColor);
        courseVideoBarrage.setCourseVideoId(courseVideoId);
        courseVideoBarrage.setContent(barrageContent);
        int ceil = (int) Math.ceil(Double.parseDouble(currentTime));
        courseVideoBarrage.setCourseVideoTime(ceil);
        Date createTime = new Date();
        courseVideoBarrage.setCreateTime(createTime);
        courseVideoBarrage.setUserId(this.user.getId());
        courseVideoBarrage.setFormatCreateTime(format(createTime, DateUtils.DATE_TIME_PATTERN.substring(5)));
        courseVideoBarrage.setFormatVideoTime(getSpecialFormatBySeconds(ceil));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "send_barrage");
        jsonObject.put("courseVideoBarrage", courseVideoBarrage);
        // 发送给前端
        userList.get(this.user.getId()).sendMessage(jsonObject.toJSONString());

        // 利用rabbiitmq实现将数据存入数据库
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        Message message = new Message(JSONObject.toJSONBytes(courseVideoBarrage), messageProperties);
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.COURSE_BARRAGE_EXCHANGE,
                RabbitmqRoutingName.COURSE_VIDEO_BARRAGE_ROUTING, message);
    }


    @OnClose
    public void OnClose() {
        if (this.user != null) {
            userList.remove(this.user.getId());
        }
    }

    // 后端向前端发送单个信息
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            throw new MonkeyBlogException(ExceptionEnum.WEBSOCKET_SEND_MESSAGE.getCode(), ExceptionEnum.WEBSOCKET_SEND_MESSAGE.getMsg());
        }
    }
}

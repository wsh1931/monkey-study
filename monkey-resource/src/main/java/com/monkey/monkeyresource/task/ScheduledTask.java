package com.monkey.monkeyresource.task;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/12 15:06
 * @version: 1.0
 * @description: 定时任务
 */
@Component
public class ScheduledTask {
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 每天凌晨4点更新资源分类
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/25 11:22
     */
//    @Scheduled(cron = "0 25 17 * * ?")
    @Scheduled(cron = "0 0 4 * * ?")
    public void communityRankTask() {
        // 更新资源分类
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.redisUpdateClassification);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.redisUpdateExchange,
                RabbitmqRoutingName.redisUpdateRouting, message);

        // 更新资源创作排行
        JSONObject userRank = new JSONObject();
        userRank.put("event", EventConstant.updateCreateResourceUserRank);
        Message message1 = new Message(userRank.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.redisUpdateExchange,
                RabbitmqRoutingName.redisUpdateRouting, message1);
    }
}

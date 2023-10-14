package com.monkey.monkeycommunity.tasks;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/25 10:54
 * @version: 1.0
 * @description: 定时任务
 */
@Component
public class ScheduledTasks {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 每天凌晨4点执行社区排行任务
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/25 11:22
     */
    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 35 9 * * ?")
    public void communityRankTask() {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateCommunityRank);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDirectExchange,
                RabbitmqRoutingName.communityDirectRouting, message);
    }
}

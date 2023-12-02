package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.constant.TipConstant;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.UserSearchService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/15 10:17
 * @version: 1.0
 * @description:
 */
@Service
public class UserSearchServiceImpl implements UserSearchService {
    @Resource
    private UserFansMapper userFansMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 关注用户
     *
     * @param concernId 被关注者id
     * @param userId 关注者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 10:20
     */
    @Override
    public R concernUser(Long concernId, long userId) {
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", concernId);
        userFansQueryWrapper.eq("fans_id", userId);
        Long selectCount = userFansMapper.selectCount(userFansQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.alreadyConcernUser);
        }

        UserFans userFans = new UserFans();
        userFans.setUserId(concernId);
        userFans.setFansId(userId);
        userFans.setCreateTime(new Date());
        userFansMapper.insert(userFans);

        // 用户关注数 + 1
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.userFansCountAddOne);
        jsonObject.put("userId", concernId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                RabbitmqRoutingName.userUpdateRouting, message);
        return R.ok();
    }

    /**
     * 取消关注用户
     *
     * @param concernId 被关注者id
     * @param userId 关注着id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 10:24
     */
    @Override
    public R cancelConcernUser(Long concernId, long userId) {
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", concernId);
        userFansQueryWrapper.eq("fans_id", userId);
        userFansMapper.delete(userFansQueryWrapper);

        // 用户关注数 - 1
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.userFansCountSubOne);
        jsonObject.put("userId", concernId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                RabbitmqRoutingName.userUpdateRouting, message);
        return R.ok();
    }
}

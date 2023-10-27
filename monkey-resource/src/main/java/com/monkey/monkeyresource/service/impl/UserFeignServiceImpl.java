package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.mapper.ResourceBuyMapper;
import com.monkey.monkeyresource.mapper.ResourceConnectMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceBuy;
import com.monkey.monkeyresource.pojo.ResourceConnect;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:26
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {

    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;
    /**
     * 删除用户购买资源记录
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 16:32
     */
    @Override
    public R deleteUserBuyResource(Long userId, Long resourceId) {
        QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
        resourceBuyQueryWrapper.eq("user_id", userId);
        resourceBuyQueryWrapper.eq("resource_id", resourceId);
        int delete = resourceBuyMapper.delete(resourceBuyQueryWrapper);
        return R.ok(delete);
    }

    /**
     * 资源收藏数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountAddOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountAddOne);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 资源收藏数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountSubOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountSubOne);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 通过资源id得到资源信息
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:41
     */
    @Override
    public R queryResourceById(Long resourceId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        resourceConnectQueryWrapper.select("type");
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);
        String type = resourceConnect.getType();
        FileTypeEnum fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(type);
        // 得到资源类型
        jsonObject.put("picture", fileUrlByFileType.getUrl());
        jsonObject.put("title", resources.getName());
        return R.ok(jsonObject);
    }
}

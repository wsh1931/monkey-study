package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.CommunityAttributeMapper;
import com.monkey.monkeycommunity.mapper.CommunityClassificationLabelMapper;
import com.monkey.monkeycommunity.mapper.CommunityLabelConnectMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityAttribute;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;
import com.monkey.monkeycommunity.pojo.CommunityLabelConnect;
import com.monkey.monkeycommunity.pojo.vo.CommunityVo;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.manage.InfoManageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/6 14:44
 * @version: 1.0
 * @description:
 */
@Service
public class InfoManageServiceImpl implements InfoManageService {
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityAttributeMapper communityAttributeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 通过社区id查询社区基本信息
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 15:18
     */
    @Override
    public R queryCommunityInfo(Long communityId) {
        Community community = communityMapper.selectById(communityId);
        CommunityVo communityVo = new CommunityVo();
        BeanUtils.copyProperties(community, communityVo);
        Long attributeLabelId = community.getAttributeLabelId();
        Long classificationId = community.getClassificationId();
        CommunityAttribute communityAttribute = communityAttributeMapper.selectById(attributeLabelId);
        communityVo.setAttributeLabel(communityAttribute.getName());
        CommunityClassificationLabel communityClassificationLabel = communityClassificationLabelMapper.selectById(classificationId);
        communityVo.setClassification(communityClassificationLabel.getName());

        // 查询内容标签列表
        QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
        communityLabelConnectQueryWrapper.eq("community_id", communityId);
        communityLabelConnectQueryWrapper.select("community_classification_label_id");
        List<Object> objectList = communityLabelConnectMapper.selectObjs(communityLabelConnectQueryWrapper);
        QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
        communityClassificationLabelQueryWrapper.in("id", objectList);
        List<CommunityClassificationLabel> communityClassificationLabelList = communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper);
        communityVo.setCommunityClassificationLabelList(communityClassificationLabelList);
        return R.ok(communityVo);
    }

    /**
     * 更新社区信息
     *
     * @param communityVoStr 社区信息字符串
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 15:59
     */
    @Override
    public R updateCommunityInfo(String communityVoStr) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateCommunityInformation);
        data.put("communityVoStr", communityVoStr);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 更新社区公告
     *
     * @param communityId 社区id
     * @param communityNotice 社区通知
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 16:29
     */
    @Override
    public R updateCommunityNotice(Long communityId, String communityNotice) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateCommunityNotice);
        data.put("communityId", communityId);
        data.put("communityNotice", communityNotice);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }
}

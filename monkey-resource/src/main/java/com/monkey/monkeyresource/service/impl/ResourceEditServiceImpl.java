package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.mapper.ResourceChargeMapper;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.ResourceEditService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/28 17:31
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceEditServiceImpl implements ResourceEditService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    /**
     * 通过资源id得到资源信息
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 10:28
     */
    @Override
    public R queryResourceById(Long resourceId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        resources.setUrl(null);
        UploadResourcesVo uploadResourcesVo = new UploadResourcesVo();
        BeanUtils.copyProperties(resources, uploadResourcesVo);

        // 得到资源标签
        String []resourceLabel = resources.getResourceLabel().split(",");
        List<String> resourceLabelList = new ArrayList<>(Arrays.asList(resourceLabel));
        uploadResourcesVo.setResourceLabelList(resourceLabelList);
        uploadResourcesVo.setType(resources.getType());
        uploadResourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resources.getType()).getUrl());
        Long resourceClassificationId = resources.getResourceClassificationId();

        LambdaQueryWrapper<ResourceClassification> resourceClassificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceClassificationLambdaQueryWrapper.eq(ResourceClassification::getId, resourceClassificationId);
        ResourceClassification resourceClassification = resourceClassificationMapper.selectOne(resourceClassificationLambdaQueryWrapper);
        List<Long> classificationIdList = new ArrayList<>();
        classificationIdList.add(resourceClassification.getParentId());
        classificationIdList.add(resources.getResourceClassificationId());

        uploadResourcesVo.setResourceClassification(classificationIdList);

        // 得到资源价格
        if (uploadResourcesVo.getFormTypeId().equals(FormTypeEnum.FORM_TYPE_TOLL.getCode())) {
            LambdaQueryWrapper<ResourceCharge> resourceChargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceChargeLambdaQueryWrapper.eq(ResourceCharge::getResourceId, resourceId);
            resourceChargeLambdaQueryWrapper.select(ResourceCharge::getPrice);
            ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeLambdaQueryWrapper);
            uploadResourcesVo.setPrice(resourceCharge.getPrice());
        }
        return R.ok(uploadResourcesVo);
    }

    /**
     * 更新资源
     *
     * @param uploadResourcesVo 更新资源vo
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 14:30
     */
    @Override
    public R updateResource(UploadResourcesVo uploadResourcesVo) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateResource);
        data.put("userId", JwtUtil.getUserId());
        data.put("uploadResourcesVo", JSONObject.toJSONString(uploadResourcesVo));
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok(TipConstant.uploadResourceSuccessWaitApproval);
    }
}

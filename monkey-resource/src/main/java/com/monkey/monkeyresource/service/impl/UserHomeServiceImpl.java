package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceConnect;
import com.monkey.monkeyresource.pojo.ResourceLike;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UserHomeService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.ResourceManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/11/23 22:38
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeServiceImpl implements UserHomeService {
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private ResourceLikeMapper resourceLikeMapper;
    @Resource
    private ResourceScoreMapper resourceScoreMapper;
    @Resource
    private ResourceDownConnectMapper resourceDownConnectMapper;

    @Resource
    private ResourceCommentMapper resourceCommentMapper;

    @Resource
    private ResourceCommentLikeMapper resourceCommentLikeMapper;

    /**
     * 通过用户id查询资源集合
     *
     * @param userId  用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 8:56
     */
    @Override
    public R queryResourceByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourcesLambdaQueryWrapper.eq(Resources::getStatus, ResourcesEnum.SUCCESS.getCode());
        resourcesLambdaQueryWrapper.eq(Resources::getUserId, userId);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        List<Resources> records = selectPage.getRecords();
        List<ResourcesVo> resourcesVoList = new ArrayList<>();
        records.stream().forEach(resource -> {
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resource, resourcesVo);

            // 得到资源类型
            LambdaQueryWrapper<ResourceConnect> resourceConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getResourceId, resource.getId());
            resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getLevel, CommonEnum.LABEL_LEVEL_TWO.getCode());
            resourceConnectLambdaQueryWrapper.select(ResourceConnect::getFormTypeId, ResourceConnect::getType);
            ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectLambdaQueryWrapper);

            resourcesVo.setFormTypeName(FormTypeEnum.getFormTypeEnum(resourceConnect.getFormTypeId()).getMsg());
            resourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl());
            resourcesVoList.add(resourcesVo);
        });

        selectPage.setRecords(resourcesVoList);
        return R.ok(selectPage);
    }

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
        UploadResourcesVo uploadResourcesVo = new UploadResourcesVo();
        BeanUtils.copyProperties(resources, uploadResourcesVo);

        // 得到资源标签
        String []resourceLabel = resources.getResourceLabel().split(",");
        List<String> resourceLabelList = new ArrayList<>(Arrays.asList(resourceLabel));
        uploadResourcesVo.setResourceLabelList(resourceLabelList);

        // 得到资源所属分类
        LambdaQueryWrapper<ResourceConnect> resourceConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getResourceId, resourceId);
        List<ResourceConnect> resourceConnectList = resourceConnectMapper.selectList(resourceConnectLambdaQueryWrapper);
        List<Long> resourceClassification = new ArrayList<>();
        for (ResourceConnect resourceConnect : resourceConnectList) {
            uploadResourcesVo.setFormTypeId(resourceConnect.getFormTypeId());
            uploadResourcesVo.setType(resourceConnect.getType());
            if (resourceConnect.getLevel().equals(CommonEnum.LABEL_LEVEL_ONE.getCode())) {
                resourceClassification.add(resourceConnect.getResourceClassificationId());
            }
        }

        for (ResourceConnect resourceConnect : resourceConnectList) {
            uploadResourcesVo.setFormTypeId(resourceConnect.getFormTypeId());
            uploadResourcesVo.setType(resourceConnect.getType());
            if (resourceConnect.getLevel().equals(CommonEnum.LABEL_LEVEL_TWO.getCode())) {
                resourceClassification.add(resourceConnect.getResourceClassificationId());
            }
        }

        uploadResourcesVo.setResourceClassification(resourceClassification);

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

    /**
     * 删除资源
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 15:16
     */
    @Override
    @Transactional
    public R deleteResource(Long resourceId) {
        resourcesMapper.deleteById(resourceId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.deleteResource);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceDeleteDirectExchange,
                RabbitmqRoutingName.resourceDeleteRouting, message);
        return R.ok();
    }
}

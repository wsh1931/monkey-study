package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.vo.ResourceClassificationVo;
import com.monkey.monkeyresource.pojo.vo.ResourceClassificationVoChild;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.monkeyresource.service.UploadResourceService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/7 17:48
 * @version: 1.0
 * @description:
 */
@Service
public class UploadResourceServiceImpl implements UploadResourceService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    /**
     * 通过文件类型得到文件类型图片
     *
     * @param fileType 文件类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/8 10:52
     */
    @Override
    public R queryFileTypeIcon(String fileType) {
        FileTypeEnum fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(fileType);
        String url = fileUrlByFileType.getUrl();
        return R.ok(url);
    }

    /**
     * 上传资源
     *
     * @param uploadResourcesVo 上传资源表单
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/10 11:27
     */
    @Override
    public R uploadResource(UploadResourcesVo uploadResourcesVo) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.uploadResource);
        data.put("userId", JwtUtil.getUserId());
        data.put("uploadResourcesVo", JSONObject.toJSONString(uploadResourcesVo));
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);
        return R.ok(TipConstant.uploadResourceSuccessWaitApproval);
    }

    /**
     * 查询联级选择器列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 16:55
     */
    @Override
    public R queryCascaderList() {
        // 最终返回集合
        List<ResourceClassificationVo> resourceClassificationVoList = new ArrayList<>();
        String redisOneKey = RedisKeyConstant.oneClassification;
        String redisTwokey = RedisKeyConstant.twoClassificationList;
        List<ResourceClassification> resourceClassificationList = JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisOneKey), ResourceClassification.class);
        for (ResourceClassification resourceClassification : resourceClassificationList) {
            ResourceClassificationVo resourceClassificationVo = new ResourceClassificationVo();
            resourceClassificationVo.setValue(resourceClassification.getId());
            resourceClassificationVo.setLabel(resourceClassification.getName());
            // 查询二级标签
            String str = stringRedisTemplate.opsForValue().get(redisTwokey + resourceClassificationVo.getValue());
            List<ResourceClassification> resourceClassifications = JSONObject.parseArray(str, ResourceClassification.class);
            List<ResourceClassificationVoChild> resourceClassificationVoChildList = new ArrayList<>();
            for (ResourceClassification classification : resourceClassifications) {
                ResourceClassificationVoChild classificationVoChild = new ResourceClassificationVoChild();
                classificationVoChild.setLabel(classification.getName());
                classificationVoChild.setValue(classification.getId());
                resourceClassificationVoChildList.add(classificationVoChild);
            }

            resourceClassificationVo.setChildren(resourceClassificationVoChildList);
            resourceClassificationVoList.add(resourceClassificationVo);
        }
        return R.ok(resourceClassificationVoList);
    }
}

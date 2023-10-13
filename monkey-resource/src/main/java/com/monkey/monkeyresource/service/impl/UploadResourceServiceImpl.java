package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UploadResourceService;
import com.monkey.spring_security.JwtUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public Object uploadResource(UploadResourcesVo uploadResourcesVo) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.uploadResource);
        data.put("userId", JwtUtil.getUserId());
        data.put("resourcesVo", JSONObject.toJSONString(uploadResourcesVo));
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);
        return R.ok(TipConstant.uploadResourceSuccessWaitApproval);
    }
}

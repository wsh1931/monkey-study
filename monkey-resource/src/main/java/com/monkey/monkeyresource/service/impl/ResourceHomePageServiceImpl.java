package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesConstant;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.mapper.ResourceChargeMapper;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.monkeyresource.service.ResourceHomePageService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/13 7:46
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceHomePageServiceImpl implements ResourceHomePageService {

    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    /**
     * 查询全部精选资源
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 8:12
     */
    @Override
    public R queryAllCurationResource() {
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        resourcesQueryWrapper.eq("is_curation", ResourcesEnum.IS_CURATION.getCode());
        resourcesQueryWrapper.orderByDesc("create_time");
        resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        resourcesQueryWrapper.last("limit " + ResourcesConstant.curationResourceLimit);
        List<Resources> resources = resourcesMapper.selectList(resourcesQueryWrapper);
        List<ResourcesVo> resourcesVoList = this.getResourceVoByResourceList(resources);
        return R.ok(resourcesVoList);
    }

    /**
     * 通过resources得到resourcesVo集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 10:56
     */
    private List<ResourcesVo> getResourceVoByResourceList(List<Resources> resources) {
        // 最终返回集合
        List<ResourcesVo> resourcesVoList = new ArrayList<>();
        for (Resources resource : resources) {
            resource.setUrl(null);
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resource, resourcesVo);
            Long resourcesVoId = resourcesVo.getId();
            resourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resource.getType()).getUrl());

            // 判断资源是否收费
            Long formTypeId = resource.getFormTypeId();
            if (FormTypeEnum.FORM_TYPE_TOLL.getCode().equals(formTypeId)) {
                // 说明是收费资源，查找资源价格
                QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
                resourceChargeQueryWrapper.eq("resource_id", resourcesVoId);
                ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeQueryWrapper);
                // 判断资源是否打折
                Integer isDiscount = resourceCharge.getIsDiscount();
                if (ResourcesEnum.IS_DISCOUNT.getCode().equals(isDiscount)) {
                    Float discount = resourceCharge.getDiscount();
                    Float price = resourceCharge.getPrice();
                    Float resourcePrice = discount * price;
                    String format = String.format("%.2f", resourcePrice);
                    resourcesVo.setPrice(format);
                } else {
                    resourcesVo.setPrice(String.valueOf(resourceCharge.getPrice()));
                }
            }
            resourcesVoList.add(resourcesVo);
        }
        return resourcesVoList;
    }

    /**
     * 通过选则标签id得到精选资源
     *
     * @param classificationId 分类id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 10:51
     */
    @Override
    public R selectCurationResource(Long classificationId) {
        LambdaQueryWrapper<ResourceClassification> resourceClassificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceClassificationLambdaQueryWrapper.eq(ResourceClassification::getParentId, classificationId);
        resourceClassificationLambdaQueryWrapper.select(ResourceClassification::getId);
        List<Object> objects = resourceClassificationMapper.selectObjs(resourceClassificationLambdaQueryWrapper);
        if (objects != null && objects.size() > 0) {
            LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourcesLambdaQueryWrapper.in(Resources::getResourceClassificationId, objects);
            resourcesLambdaQueryWrapper.eq(Resources::getStatus, ResourcesEnum.SUCCESS.getCode());
            resourcesLambdaQueryWrapper.last("limit " + ResourcesConstant.hottestResourceLimit);
            resourcesLambdaQueryWrapper.eq(Resources::getIsCuration, ResourcesEnum.IS_CURATION.getCode());
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesLambdaQueryWrapper);
            List<ResourcesVo> resourceVoByResourceList = this.getResourceVoByResourceList(resourcesList);
            return R.ok(resourceVoByResourceList);
        } else {
            return R.ok();
        }
    }

    /**
     * 查询全部下载次数最多资源
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 11:18
     */
    @Override
    public R queryAllHottestResource() {
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        resourcesQueryWrapper.orderByDesc("down_count");
        resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        resourcesQueryWrapper.last("limit " + ResourcesConstant.curationResourceLimit);
        List<Resources> resources = resourcesMapper.selectList(resourcesQueryWrapper);
        List<ResourcesVo> resourcesVoList = this.getResourceVoByResourceList(resources);
        return R.ok(resourcesVoList);
    }

    /**
     * 通过选则标签id得到下载次数最多资源
     *
     * @param classificationId 分类id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 11:19
     */
    @Override
    public R selectHottestResource(Long classificationId) {
        LambdaQueryWrapper<ResourceClassification> resourceClassificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceClassificationLambdaQueryWrapper.eq(ResourceClassification::getParentId, classificationId);
        resourceClassificationLambdaQueryWrapper.select(ResourceClassification::getId);
        List<Object> objects = resourceClassificationMapper.selectObjs(resourceClassificationLambdaQueryWrapper);
        if (objects != null && objects.size() > 0) {
            LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourcesLambdaQueryWrapper.in(Resources::getResourceClassificationId, objects);
            resourcesLambdaQueryWrapper.eq(Resources::getStatus, ResourcesEnum.SUCCESS.getCode());
            resourcesLambdaQueryWrapper.last("limit " + ResourcesConstant.hottestResourceLimit);
            resourcesLambdaQueryWrapper.orderByDesc(Resources::getDownCount);
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesLambdaQueryWrapper);
            List<ResourcesVo> resourceVoByResourceList = this.getResourceVoByResourceList(resourcesList);
            return R.ok(resourceVoByResourceList);
        } else {
            return R.ok();
        }
    }

    /**
     * 查询最新资源集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 11:29
     */
    @Override
    public R queryLatestResource() {
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        resourcesQueryWrapper.orderByDesc("create_time");
        resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        List<Resources> resources = resourcesMapper.selectList(resourcesQueryWrapper);
        List<ResourcesVo> resourcesVoList = getResourceVoByResourceList(resources);
        for (ResourcesVo resourcesVo : resourcesVoList) {
            Long userId = resourcesVo.getUserId();
            User user = userMapper.selectById(userId);
            resourcesVo.setUsername(user.getUsername());
            resourcesVo.setHeadImg(user.getPhoto());
        }
        return R.ok(resourcesVoList);
    }

    /**
     * 查询资源创作用户排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/13 17:19
     */
    @Override
    public R queryUserRank() {
        String redisKey = RedisKeyConstant.createResourceUserRank;
        String s = stringRedisTemplate.opsForValue().get(redisKey);
        return R.ok(JSONObject.parse(s));
    }

    /**
     * 资源游览数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/23 8:03
     */
    @Override
    public R resourceViewCountAddOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceViewCountAddOne);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("userId", JwtUtil.getUserId());
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }
}

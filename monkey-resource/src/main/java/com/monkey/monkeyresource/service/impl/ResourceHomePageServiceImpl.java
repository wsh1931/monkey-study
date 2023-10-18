package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesConstant;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.mapper.ResourceChargeMapper;
import com.monkey.monkeyresource.mapper.ResourceConnectMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceConnect;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.monkeyresource.service.ResourceHomePageService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
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
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resource, resourcesVo);
            Long resourcesVoId = resourcesVo.getId();
            // 得到资源类型
            QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
            resourceConnectQueryWrapper.eq("resource_id", resourcesVoId);
            resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
            ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);
            String fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl();
            resourcesVo.setTypeUrl(fileUrlByFileType);
            resourcesVo.setType(resourceConnect.getType());
            resourcesVo.setFormTypeId(resourceConnect.getFormTypeId());

            // 判断资源是否收费
            Long formTypeId = resourcesVo.getFormTypeId();
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
        QueryWrapper<ResourceConnect> resourceClassificationConnectQueryWrapper = new QueryWrapper<>();
        resourceClassificationConnectQueryWrapper.eq("resource_classification_id", classificationId);
        resourceClassificationConnectQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        resourceClassificationConnectQueryWrapper.last("limit " + ResourcesConstant.curationResourceLimit);
        resourceClassificationConnectQueryWrapper.select("resource_id");
        List<Object> objects = resourceConnectMapper.selectObjs(resourceClassificationConnectQueryWrapper);
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        if (objects != null && objects.size() > 0) {
            resourcesQueryWrapper.in("id", objects);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            resourcesQueryWrapper.eq("is_curation", ResourcesEnum.IS_CURATION.getCode());
            resourcesQueryWrapper.orderByDesc("create_time");
            List<Resources> resources = resourcesMapper.selectList(resourcesQueryWrapper);
            List<ResourcesVo> resourceVoByResourceList = this.getResourceVoByResourceList(resources);
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
        QueryWrapper<ResourceConnect> resourceClassificationConnectQueryWrapper = new QueryWrapper<>();
        resourceClassificationConnectQueryWrapper.eq("resource_classification_id", classificationId);
        resourceClassificationConnectQueryWrapper.last("limit " + ResourcesConstant.curationResourceLimit);
        resourceClassificationConnectQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        resourceClassificationConnectQueryWrapper.select("resource_id");
        List<Object> objects = resourceConnectMapper.selectObjs(resourceClassificationConnectQueryWrapper);
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        if (objects != null && objects.size() > 0) {
            resourcesQueryWrapper.in("id", objects);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            resourcesQueryWrapper.orderByDesc("down_count");
            List<Resources> resources = resourcesMapper.selectList(resourcesQueryWrapper);
            List<ResourcesVo> resourceVoByResourceList = this.getResourceVoByResourceList(resources);
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
}

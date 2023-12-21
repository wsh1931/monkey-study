package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.FormTypeMapper;
import com.monkey.monkeyUtils.pojo.FormType;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.mapper.ResourceChargeMapper;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.redis.RedisKeyConstant;
import com.monkey.monkeyresource.service.ResourceSearchService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: wusihao
 * @date: 2023/10/14 14:01
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceSearchServiceImpl implements ResourceSearchService {
    @Resource
    private FormTypeMapper formTypeMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;

    /**
     * 查询形式类型列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 14:11
     */
    @Override
    public R queryFormTypeList() {
        QueryWrapper<FormType> formTypeQueryWrapper = new QueryWrapper<>();
        formTypeQueryWrapper.orderByAsc("sort");
        List<FormType> formTypes = formTypeMapper.selectList(formTypeQueryWrapper);
        Iterator<FormType> iterator = formTypes.listIterator();
        // 删除官方推荐
        while (iterator.hasNext()) {
            FormType next = iterator.next();
            if (FormTypeEnum.FORM_TYPE_COMMEND.getCode().equals(next.getId())) {
                iterator.remove();
            }
        }
        return R.ok(formTypes);
    }

    /**
     * 得到方向集合(包括全部)
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 14:40
     */
    @Override
    public R queryDirectList() {
        String redisKey = RedisKeyConstant.oneClassification;
        List<ResourceClassification> resourceClassificationList = JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey), ResourceClassification.class);
        ResourceClassification resourceClassification = new ResourceClassification();
        resourceClassification.setId(FormTypeEnum.FORM_TYPE_ALL.getCode());
        resourceClassification.setName(FormTypeEnum.FORM_TYPE_ALL.getMsg());
        resourceClassificationList.add(0, resourceClassification);
        return R.ok(resourceClassificationList);
    }

    /**
     * 通过方向集合查询分类集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 15:17
     */
    @Override
    public R queryClassificationByDirectId(Long directId) {
        if (!directId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            String redisKey = RedisKeyConstant.twoClassificationList + directId;
            String data = stringRedisTemplate.opsForValue().get(redisKey);
            List<ResourceClassification> resourceClassificationList = JSONObject.parseArray(data, ResourceClassification.class);
            ResourceClassification resourceClassification = new ResourceClassification();
            resourceClassification.setId(FormTypeEnum.FORM_TYPE_ALL.getCode());
            resourceClassification.setName(FormTypeEnum.FORM_TYPE_ALL.getMsg());
            resourceClassificationList.add(0, resourceClassification);
            return R.ok(resourceClassificationList);
        }

        return R.ok();
    }

    /**
     * 查询格式列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 15:30
     */
    @Override
    public R queryFormatList() {
        FileTypeEnum[] values = FileTypeEnum.values();
        List<String> fileTypeList = new ArrayList<>();
        fileTypeList.add(String.valueOf(FormTypeEnum.FORM_TYPE_ALL.getMsg()));
        for (FileTypeEnum value : values) {
            fileTypeList.add(value.getType());
        }
        return R.ok(fileTypeList);
    }

    /**
     * 查找最热资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param formTypeId 形式类型id
     * @param directionId 方向id
     * @param classificationId 分类id
     * @param format 格式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 16:29
     */
    @Override
    public R queryHottestResourceList(Long currentPage,
                                      Integer pageSize,
                                      Long formTypeId,
                                      Long directionId,
                                      Long classificationId,
                                      String format,
                                      String resourceName) {
        // 通过条件查询资源
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getLikeCount);
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getViewCount);
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getDownCount);

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);

        Long total = selectPage.getTotal();
        if (total > 0) {
            List<Resources> resourcesList = selectPage.getRecords();
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList);
            selectPage.setRecords(resourcesVoList);
        }

        return R.ok(selectPage);
    }

    /**
     * 查找最新资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param formTypeId 形式类型id
     * @param directionId 方向id
     * @param classificationId 分类id
     * @param format 格式
     * @param resourceName 模糊查询资源名
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 11:10
     */
    @Override
    public R queryLatestResourceList(Long currentPage,
                                     Integer pageSize,
                                     Long formTypeId,
                                     Long directionId,
                                     Long classificationId,
                                     String format,
                                     String resourceName) {
        // 通过条件查询资源
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getCreateTime);

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);

        Long total = selectPage.getTotal();
        if (total > 0) {
            List<Resources> resourcesList = selectPage.getRecords();
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList);
            selectPage.setRecords(resourcesVoList);
        }

        return R.ok(selectPage);
    }

    /**
     * 得到升序价格列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param formTypeId 形式类型id
     * @param directionId 方向id
     * @param classificationId 分类id
     * @param format 格式
     * @param  resourceName 模糊查询资源名
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 11:11
     */
    @Override
    public R queryAscPriceResourceList(Long currentPage,
                                       Integer pageSize,
                                       Long formTypeId,
                                       Long directionId,
                                       Long classificationId,
                                       String format,
                                       String resourceName) {
        // 通过条件查询资源
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        long total = selectPage.getTotal();
        if (total > 0) {
            List<Resources> resourcesList = selectPage.getRecords();
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList);

            resourcesVoList.sort(Comparator.comparing(ResourcesVo::getPrice));
            selectPage.setRecords(resourcesVoList);
        }

        return R.ok(selectPage);
    }

    /**
     * 得到降序价格列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param formTypeId 形式类型id
     * @param directionId 方向id
     * @param classificationId 分类id
     * @param  resourceName 模糊查询资源名
     * @param format 格式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 11:15
     */
    @Override
    public R queryDescPriceResourceList(Long currentPage,
                                        Integer pageSize,
                                        Long formTypeId,
                                        Long directionId,
                                        Long classificationId,
                                        String format,
                                        String resourceName) {
        // 通过条件查询资源
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        long total = selectPage.getTotal();
        if (total > 0) {
            List<Resources> resourcesList = selectPage.getRecords();
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList);

            resourcesVoList.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
            selectPage.setRecords(resourcesVoList);
        }

        return R.ok(selectPage);
    }

    /**
     * 得到resourceVos集合通过resources集合
     *
     * @param resourcesList 资源集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 10:05
     */
    private List<ResourcesVo> getReourceVoByResourcesLsit(List<Resources> resourcesList) {
        // 最终返回集合
        List<ResourcesVo> resourcesVoList = new ArrayList<>();
        for (Resources resources : resourcesList) {
            Long resourcesId = resources.getId();
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resources, resourcesVo);
            String fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(resources.getType()).getUrl();
            resourcesVo.setTypeUrl(fileUrlByFileType);
            Long formTypeId = resources.getFormTypeId();

            // 判断资源是否收费
            if (FormTypeEnum.FORM_TYPE_TOLL.getCode().equals(formTypeId)) {
                // 说明是收费资源，查找资源价格
                QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
                resourceChargeQueryWrapper.eq("resource_id", resourcesId);
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
     * 通过条件查询资源
     *
     * * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param formTypeId 形式类型id
     * @param directionId 方向id
     * @param classificationId 分类id
     * @param format 格式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 10:00
     */
    private LambdaQueryWrapper<Resources> getResourceConnectByCondition(Long currentPage, Integer pageSize, Long formTypeId, Long directionId, Long classificationId, String format) {
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (formTypeId != null && !formTypeId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            resourcesLambdaQueryWrapper.eq(Resources::getFormTypeId, formTypeId);
        }
        if (directionId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            // 若一级标签为全部

        } else if (classificationId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            // 若一级标签不为全部，二级标签为全部
            // 查询一级标签下的所有二级标签
            LambdaQueryWrapper<ResourceClassification> resourceClassificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceClassificationLambdaQueryWrapper.eq(ResourceClassification::getParentId, directionId);
            resourceClassificationLambdaQueryWrapper.select(ResourceClassification::getId);
            List<Object> objects = resourceClassificationMapper.selectObjs(resourceClassificationLambdaQueryWrapper);
            if (objects != null && objects.size() > 0) {
                resourcesLambdaQueryWrapper.in(Resources::getResourceClassificationId, objects);
            }
        } else {
            // 若一级，二级标签均不为全部
            resourcesLambdaQueryWrapper.eq(Resources::getResourceClassificationId, classificationId);
        }

        if (!format.equals(FormTypeEnum.FORM_TYPE_ALL.getMsg())) {
            resourcesLambdaQueryWrapper.eq(Resources::getType, format);
        }
        resourcesLambdaQueryWrapper.eq(Resources::getStatus, ResourcesEnum.SUCCESS.getCode());
        return resourcesLambdaQueryWrapper;
    }

}

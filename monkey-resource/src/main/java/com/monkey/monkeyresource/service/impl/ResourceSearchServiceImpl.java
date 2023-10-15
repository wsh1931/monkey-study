package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import com.monkey.monkeyresource.mapper.ResourceConnectMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.ResourceConnect;
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
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;

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
        Page selectPage = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        Long total = selectPage.getTotal();
        if (total > 0) {
            List<ResourceConnect> resourceConnectList =  selectPage.getRecords();
            // 得到资源id集合，防止每次查询数据库，哈希表用来找到资源所对应的resourceConnect
            List<Long> resourcesIdList = new ArrayList<>();
            Map<Long, ResourceConnect> hash = new HashMap<>();
            for (ResourceConnect resourceConnect : resourceConnectList) {
                Long resourceId = resourceConnect.getResourceId();
                hash.put(resourceId, resourceConnect);
                resourcesIdList.add(resourceId);
            }

            QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
            resourcesQueryWrapper.in("id", resourcesIdList);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            resourcesQueryWrapper.orderByDesc("down_count");
            resourcesQueryWrapper.orderByDesc("view_count");
            if (resourceName != null && !"".equals(resourceName)) {
                resourcesQueryWrapper.like("name", resourceName);
            }
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList, hash);
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
        Page selectPage = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        Long total = selectPage.getTotal();
        if (total > 0) {
            List<ResourceConnect> resourceConnectList =  selectPage.getRecords();
            // 得到资源id集合，防止每次查询数据库，哈希表用来找到资源所对应的resourceConnect
            List<Long> resourcesIdList = new ArrayList<>();
            Map<Long, ResourceConnect> hash = new HashMap<>();
            for (ResourceConnect resourceConnect : resourceConnectList) {
                Long resourceId = resourceConnect.getResourceId();
                hash.put(resourceId, resourceConnect);
                resourcesIdList.add(resourceId);
            }

            QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
            resourcesQueryWrapper.in("id", resourcesIdList);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            resourcesQueryWrapper.orderByDesc("create_time");
            if (resourceName != null && !"".equals(resourceName)) {
                resourcesQueryWrapper.like("name", resourceName);
            }
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList, hash);
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
        Page selectPage = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        Long total = selectPage.getTotal();
        if (total > 0) {
            List<ResourceConnect> resourceConnectList =  selectPage.getRecords();
            // 得到资源id集合，防止每次查询数据库，哈希表用来找到资源所对应的resourceConnect
            List<Long> resourcesIdList = new ArrayList<>();
            Map<Long, ResourceConnect> hash = new HashMap<>();
            for (ResourceConnect resourceConnect : resourceConnectList) {
                Long resourceId = resourceConnect.getResourceId();
                hash.put(resourceId, resourceConnect);
                resourcesIdList.add(resourceId);
            }

            QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
            resourcesQueryWrapper.in("id", resourcesIdList);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            if (resourceName != null && !"".equals(resourceName)) {
                resourcesQueryWrapper.like("name", resourceName);
            }
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList, hash);

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
        Page selectPage = getResourceConnectByCondition(currentPage, pageSize, formTypeId, directionId, classificationId, format);
        Long total = selectPage.getTotal();
        if (total > 0) {
            List<ResourceConnect> resourceConnectList =  selectPage.getRecords();
            // 得到资源id集合，防止每次查询数据库，哈希表用来找到资源所对应的resourceConnect
            List<Long> resourcesIdList = new ArrayList<>();
            Map<Long, ResourceConnect> hash = new HashMap<>();
            for (ResourceConnect resourceConnect : resourceConnectList) {
                Long resourceId = resourceConnect.getResourceId();
                hash.put(resourceId, resourceConnect);
                resourcesIdList.add(resourceId);
            }

            QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
            resourcesQueryWrapper.in("id", resourcesIdList);
            resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
            if (resourceName != null && !"".equals(resourceName)) {
                resourcesQueryWrapper.like("name", resourceName);
            }
            List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
            // 得到resourceVos集合通过resources集合
            List<ResourcesVo> resourcesVoList = getReourceVoByResourcesLsit(resourcesList, hash);

            resourcesVoList.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
            selectPage.setRecords(resourcesVoList);
        }

        return R.ok(selectPage);
    }

    /**
     * 得到resourceVos集合通过resources集合
     *
     * @param resourcesList 资源集合
     * @param hash 资源id对应的ResourceConnect类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/15 10:05
     */
    private List<ResourcesVo> getReourceVoByResourcesLsit(List<Resources> resourcesList, Map<Long, ResourceConnect> hash) {
        // 最终返回集合
        List<ResourcesVo> resourcesVoList = new ArrayList<>();
        for (Resources resources : resourcesList) {
            Long resourcesId = resources.getId();
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resources, resourcesVo);
            ResourceConnect resourceConnect = hash.get(resourcesId);
            String type = resourceConnect.getType();
            Long formTypeId = resourceConnect.getFormTypeId();
            resourcesVo.setFormTypeId(formTypeId);
            resourcesVo.setType(type);
            String fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl();
            resourcesVo.setTypeUrl(fileUrlByFileType);

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
    private Page getResourceConnectByCondition(Long currentPage, Integer pageSize, Long formTypeId, Long directionId, Long classificationId, String format) {
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        if (formTypeId != null && !formTypeId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            resourceConnectQueryWrapper.eq("form_type_id", formTypeId);
        }
        if (directionId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            // 若一级标签为全部
            resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        } else if (classificationId.equals(FormTypeEnum.FORM_TYPE_ALL.getCode())) {
            // 若一级标签不为全部，二级标签为全部
            resourceConnectQueryWrapper.eq("resource_classification_id", directionId);
        } else {
            // 若一级，二级标签均不为全部
            resourceConnectQueryWrapper.eq("resource_classification_id", classificationId);
        }

        if (!format.equals(FormTypeEnum.FORM_TYPE_ALL.getMsg())) {
            resourceConnectQueryWrapper.eq("type", format);
        }

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourceConnectMapper.selectPage(page, resourceConnectQueryWrapper);

        return selectPage;
    }

}

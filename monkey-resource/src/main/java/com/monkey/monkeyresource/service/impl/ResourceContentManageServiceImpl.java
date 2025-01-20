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
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.mapper.ResourceStatisticsMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceStatistics;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourceConditionVo;
import com.monkey.monkeyresource.service.ResourceContentManageService;
import io.swagger.models.auth.In;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/12/19 15:08
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceContentManageServiceImpl implements ResourceContentManageService {
    @Resource
    private FormTypeMapper formTypeMapper;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceStatisticsMapper resourceStatisticsMapper;

    /**
     * 查询资源类型集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/19 15:09
     */
    @Override
    public R queryResourceType() {
        FileTypeEnum[] values = FileTypeEnum.values();
        List<String> fileTypeList = new ArrayList<>();
        for (FileTypeEnum value : values) {
            fileTypeList.add(value.getType());
        }
        return R.ok(fileTypeList);
    }

    /**
     * 查询形式类型集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/19 15:17
     */
    @Override
    public R queryFormType() {
        QueryWrapper<FormType> formTypeQueryWrapper = new QueryWrapper<>();
        formTypeQueryWrapper.orderByAsc("sort");
        List<FormType> formTypes = formTypeMapper.selectList(formTypeQueryWrapper);
        Iterator<FormType> iterator = formTypes.listIterator();
        // 删除全部
        while (iterator.hasNext()) {
            FormType next = iterator.next();
            if (FormTypeEnum.FORM_TYPE_ALL.getCode().equals(next.getId())) {
                iterator.remove();
            }
        }
        return R.ok(formTypes);
    }

    /**
     * 通过条件查询资源列表
     *
     * @param resourceConditionVo 条件实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/19 20:43
     */
    @Override
    public R queryResourceByCondition(ResourceConditionVo resourceConditionVo) {
        Long currentPage = resourceConditionVo.getCurrentPage();
        Integer pageSize = resourceConditionVo.getPageSize();
        String type = resourceConditionVo.getType();
        Integer status = resourceConditionVo.getStatus();
        List<Date> dateList = resourceConditionVo.getDateList();
        Long formTypeId = resourceConditionVo.getFormTypeId();
        List<Long> resourceClassification = resourceConditionVo .getResourceClassification();
        String name = resourceConditionVo.getName();
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourcesLambdaQueryWrapper.eq(Resources::getUserId, JwtUtil.getUserId());
        resourcesLambdaQueryWrapper.like(name != null && !"".equals(name), Resources::getName, name);
        resourcesLambdaQueryWrapper.eq(type != null && !"".equals(type), Resources::getType, type);
        resourcesLambdaQueryWrapper.eq(status != null, Resources::getStatus, status);
        resourcesLambdaQueryWrapper.eq(formTypeId != null, Resources::getFormTypeId, formTypeId);
        if (resourceClassification != null &&  resourceClassification.size() > 1) {
            resourcesLambdaQueryWrapper.eq(Resources::getResourceClassificationId, resourceClassification.get(1));
        }
        if (dateList != null && dateList.size() > 1) {
            resourcesLambdaQueryWrapper.ge(Resources::getCreateTime, dateList.get(0));
            resourcesLambdaQueryWrapper.le(Resources::getCreateTime, dateList.get(1));
        }

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        List<Resources> records = selectPage.getRecords();
        records.forEach(resource -> {
            String type1 = resource.getType();
            resource.setTypeUrl(FileTypeEnum.getFileUrlByFileType(type1).getUrl());
            resource.setFormTypeName(FormTypeEnum.getFormTypeEnum(resource.getFormTypeId()).getMsg());
        });
        return R.ok(selectPage);
    }

    /**
     * 查询资源
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/20 11:38
     */
    @Override
    public R queryResource(ResourceConditionVo resourceConditionVo) {
        Long currentPage = resourceConditionVo.getCurrentPage();
        Integer pageSize = resourceConditionVo.getPageSize();
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourcesLambdaQueryWrapper.eq(Resources::getUserId, JwtUtil.getUserId());
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        List<Resources> records = selectPage.getRecords();
        records.forEach(resource -> {
            String type = resource.getType();
            resource.setTypeUrl(FileTypeEnum.getFileUrlByFileType(type).getUrl());
            resource.setFormTypeName(FormTypeEnum.getFormTypeEnum(resource.getFormTypeId()).getMsg());
        });
        return R.ok(selectPage);
    }

    /**
     * 查询资源近一周的资源数据
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/21 16:56
     */
    @Override
    public R queryResourceDataRecentWeek(Long resourceId) {
        // 得到一周前的时间
        String before = DateUtils.format(DateUtils.addDateDays(new Date(), -6));
        String now = DateUtils.format(new Date());
        LambdaQueryWrapper<ResourceStatistics> resourceStatisticsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceStatisticsLambdaQueryWrapper
                .eq(ResourceStatistics::getResourceId, resourceId)
                .ge(ResourceStatistics::getCreateTime, before)
                .le(ResourceStatistics::getCreateTime, now);
        List<ResourceStatistics> resourceStatisticsList = resourceStatisticsMapper.selectList(resourceStatisticsLambdaQueryWrapper);

        // 通过时间排序
        resourceStatisticsList.sort(Comparator.comparing(ResourceStatistics::getCreateTime));

        // 游览数集合
        List<Integer> viewList = new ArrayList<>();
        // 点赞数集合
        List<Integer> likeList = new ArrayList<>();
        // 评论数集合
        List<Integer> commentList = new ArrayList<>();
        // 下载数集合
        List<Integer> downList = new ArrayList<>();
        // 购买数集合
        List<Integer> buyList = new ArrayList<>();
        // 收藏数集合
        List<Integer> collectList = new ArrayList<>();
        Date date = DateUtils.addDateDays(new Date(), -6);

        // 得到近 7 天时间数集合
        List<String> dateList = DateSelfUtils
                .getBeenTwoDayAllDate(date, new Date()).stream()
                .map(DateUtils::format)
                .collect(Collectors.toList());

        Set<String> dateSet = new HashSet<>();
        // 计算出有哪些日期资源存在操作（点赞，游览等）
        resourceStatisticsList.forEach(resource -> dateSet.add(DateUtils.format(resource.getCreateTime())));

        int cnt = 0;
        for (String d : dateList) {
            // 若该日期存在操作
            if (dateSet.contains(d)) {
                ResourceStatistics resourceStatistics = resourceStatisticsList.get(cnt);
                viewList.add(resourceStatistics.getViewCount());
                likeList.add(resourceStatistics.getLikeCount());
                commentList.add(resourceStatistics.getCommentCount());
                downList.add(resourceStatistics.getDownCount());
                buyList.add(resourceStatistics.getBuyCount());
                collectList.add(resourceStatistics.getCollectCount());

                // 数据经过了排序，可以直接指向下一个
                cnt ++ ;
            } else {
                // 若不存在操作, 则加入0
                viewList.add(0);
                likeList.add(0);
                commentList.add(0);
                downList.add(0);
                buyList.add(0);
                collectList.add(0);
            }
        }

        JSONObject data = new JSONObject();
        data.put("viewList", viewList);
        data.put("likeList", likeList);
        data.put("commentList", commentList);
        data.put("downList", downList);
        data.put("buyList", buyList);
        data.put("collectList", collectList);
        data.put("dateList", dateList);
        return R.ok(data);
    }
}

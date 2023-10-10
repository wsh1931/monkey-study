package com.monkey.monkeyresource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.mapper.ResourceClassificationConnectMapper;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.ResourceClassificationConnect;
import com.monkey.monkeyresource.service.ResourceClassificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/10 9:19
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceClassificationServiceImpl implements ResourceClassificationService {
    @Resource
    private ResourceClassificationConnectMapper resourceClassificationConnectMapper;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    /**
     * 得到一级标签
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/10 9:23
     */
    @Override
    public R queryOneLevelClassificationList() {
        QueryWrapper<ResourceClassification> resourceClassificationQueryWrapper = new QueryWrapper<>();
        resourceClassificationQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        resourceClassificationQueryWrapper.orderByAsc("sort");
        List<ResourceClassification> resourceClassificationList = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);
        return R.ok(resourceClassificationList);
    }

    /**
     * 通过搜索字段得到一级标签
     *
     * @param oneClassificationName 一级分类搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/10 9:34
     */
    @Override
    public R queryOneClassification(String oneClassificationName) {
        QueryWrapper<ResourceClassification> resourceClassificationQueryWrapper = new QueryWrapper<>();
        resourceClassificationQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        resourceClassificationQueryWrapper.orderByAsc("sort");
        if (oneClassificationName == null || "".equals(oneClassificationName)) {
            List<ResourceClassification> resourceClassificationList = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);
            return R.ok(resourceClassificationList);
        }

        resourceClassificationQueryWrapper.like("name", oneClassificationName);
        List<ResourceClassification> resourceClassificationList = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);
        return R.ok(resourceClassificationList);
    }

    /**
     * 通过一级分类id得到二级分类列表
     *
     * @param classificationOneId 一级分类id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/10 9:40
     */
    @Override
    public R queryTwoClassificationListByOneLabelId(Long classificationOneId) {
        QueryWrapper<ResourceClassification> resourceClassificationQueryWrapper = new QueryWrapper<>();
        resourceClassificationQueryWrapper.eq("parent_id", classificationOneId);
        resourceClassificationQueryWrapper.orderByAsc("sort");
        List<ResourceClassification> resourceClassificationList = resourceClassificationMapper.selectList(resourceClassificationQueryWrapper);
        return R.ok(resourceClassificationList);
    }
}

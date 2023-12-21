package com.monkey.monkeyresource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.mapper.ResourceClassificationMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.service.SearchFeignService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/12 17:19
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询所有资源
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 17:22
     */
    @Override
    public R queryAllResource() {
        QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
        resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
        resourcesList.parallelStream().forEach(resource -> {
            Long resourceId = resource.getId();
            resource.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resource.getType()).getUrl());
            resource.setFormTypeName(FormTypeEnum.getFormTypeEnum(resource.getFormTypeId()).getMsg());
            List<Long> labelIdList = new ArrayList<>();
            labelIdList.add(resource.getResourceClassificationId());
            Long resourceClassificationId = resource.getResourceClassificationId();
            ResourceClassification resourceClassification = resourceClassificationMapper.selectById(resourceClassificationId);
            List<String> name = new ArrayList<>();
            name.add(resourceClassification.getName());
            resource.setResourceClassificationName(name);

            // 得到资源标签
            String resourceLabel = resource.getResourceLabel();
            if (resourceLabel != null && !"".equals(resourceLabel)) {
                String[] split = resourceLabel.split(",");
                List<String> labelList = new ArrayList<>(Arrays.asList(split));
                resource.setResourceLabelName(labelList);
            }

            // 查询用户信息
            User user = userMapper.selectById(resource.getUserId());
            resource.setUsername(user.getUsername());
            resource.setUserBrief(user.getBrief());
            resource.setUserHeadImg(user.getPhoto());
        });
        return R.ok(resourcesList);
    }

    /**
     * 得到所有用户所有资源，点赞，收藏，游览数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 11:03
     */
    @Override
    public R queryAllUserResourceInfo() {
        QueryWrapper<Resources> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        articleQueryWrapper.groupBy("user_id");
        articleQueryWrapper.select("user_id",
                "sum(collect_count) as collectCount",
                "sum(view_count) as viewCount",
                "sum(like_count) as likeCount",
                "count(*) as opusCount"
        );
        List<Map<String, Object>> maps = resourcesMapper.selectMaps(articleQueryWrapper);
        return R.ok(maps);
    }
}

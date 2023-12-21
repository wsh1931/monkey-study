package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.ResourceConditionVo;

public interface ResourceContentManageService {
    // 查询资源类型集合
    R queryResourceType();

    // 查询形式类型集合
    R queryFormType();

    // 通过条件查询资源列表
    R queryResourceByCondition(ResourceConditionVo resourceConditionVo);

    // 查询资源
    R queryResource(ResourceConditionVo resourceConditionVo);

    // 查询资源近一周的资源数据
    R queryResourceDataRecentWeek(Long resourceId);
}

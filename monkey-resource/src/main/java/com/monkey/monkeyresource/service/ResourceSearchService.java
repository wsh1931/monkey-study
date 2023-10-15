package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface ResourceSearchService {
    // 查询形式类型列表
    R queryFormTypeList();

    // 得到方向集合(包括全部)
    R queryDirectList();

    // 通过方向集合查询分类集合
    R queryClassificationByDirectId(Long directId);

    // 查询格式列表
    R queryFormatList();

    // 查找最热资源列表
    R queryHottestResourceList(Long currentPage,
                               Integer pageSize,
                               Long formTypeId,
                               Long directionId,
                               Long classificationId,
                               String format,
                               String resourceName);

    // 查找最新资源列表
    R queryLatestResourceList(Long currentPage,
                              Integer pageSize,
                              Long formTypeId,
                              Long directionId,
                              Long classificationId,
                              String format,
                              String resourceName);

    // 得到升序价格列表
    R queryAscPriceResourceList(Long currentPage,
                                Integer pageSize,
                                Long formTypeId,
                                Long directionId,
                                Long classificationId,
                                String format,
                                String resourceName);

    // 得到降序价格列表
    R queryDescPriceResourceList(Long currentPage,
                                 Integer pageSize,
                                 Long formTypeId,
                                 Long directionId,
                                 Long classificationId,
                                 String format,
                                 String resourceName);
}

package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface ResourceClassificationService {
    // 得到一级标签
    R queryOneLevelClassificationList();

    // 通过搜索字段得到一级标签
    R queryOneClassification(String oneClassificationName);

    // 通过一级分类id得到二级分类列表
    R queryTwoClassificationListByOneLabelId(Long classificationOneId);
}

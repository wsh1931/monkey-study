package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;

public interface CommunityService {
    // 得到一级标签
    R getOneLevelLabelList();

    // 通过一级标签id得到二级标签列表
    R getTwoLabelListByOneLabelId(long labelOneId);
}

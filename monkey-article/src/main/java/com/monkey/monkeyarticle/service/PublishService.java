package com.monkey.monkeyarticle.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface PublishService {
    // 发布文章
    ResultVO publishArticle(String content, String profile, String photo, String title, String labelId);

    // 得到一级标签列表
    ResultVO getOneLevelLabelList();

    // 通过一级标签id查询二级标签列表
    ResultVO getTwoLabelListByOneLabelId(Long labelOneId);
}

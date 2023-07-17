package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface CourseService {

    // 得到一级标签列表
    ResultVO getOneLabelList();

    // 通过一级标签id得到二级标签
    ResultVO getTwoLabelListByOneLabelId(Long oneLabelId);
}

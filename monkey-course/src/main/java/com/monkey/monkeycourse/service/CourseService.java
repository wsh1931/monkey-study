package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;

public interface CourseService {

    // 得到一级标签列表
    ResultVO getOneLabelList();

    // 通过一级标签id得到二级标签
    ResultVO getTwoLabelListByOneLabelId(Long oneLabelId);

    // 通过二级标签分页查询文章列表
    ResultVO getCourseListByTwoLabelId(long formTypeId, long twoLabelId, long currentPage, long pageSize);

    // 得到形式类型集合
    R getFormTypeList();
}

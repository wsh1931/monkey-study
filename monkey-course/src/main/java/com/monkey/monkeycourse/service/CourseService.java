package com.monkey.monkeycourse.service;

import com.monkey.monkeyUtils.result.ResultVO;

public interface CourseService {

    // 得到一级标签列表
    ResultVO getOneLabelList();

    // 通过一级标签id得到二级标签
    ResultVO getTwoLabelListByOneLabelId(Long oneLabelId);

    // 通过二级标签分页查询文章列表
    ResultVO getCourseListByTwoLabelId(long twoLabelId, long currentPage, long pageSize);
}

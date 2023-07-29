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

    // 通过形式id和一级标签id, 二级标签id得到所有最热课程列表
    R getFireCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize);

    // 通过形式id和一级标签id, 二级标签id得到所有最新课程列表
    R getLastlyCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize);

    // 通过形式id和一级标签id, 二级标签id得到升序价格列表
    R getAscPriceCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize);

    // 通过形式id和一级标签id, 二级标签id得到降序价格列表
    R getDescPriceCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize);
}

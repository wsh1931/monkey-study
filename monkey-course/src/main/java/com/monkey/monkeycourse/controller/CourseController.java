package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeycourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/15 20:41
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 得到形式类型集合
    @GetMapping("/getFormTypeList")
    public R getFormTypeList() {
        return courseService.getFormTypeList();
    }

    // 得到一级标签列表
    @GetMapping("/getOneLabelList")
    public ResultVO getOneLabelList() {
        return courseService.getOneLabelList();
    }

    // 通过一级标签id得到二级标签
    @GetMapping("/getTwoLabelListByOneLabelId")
    public ResultVO getTwoLabelListByOneLabelId(@RequestParam Map<String, String> data) {
        Long oneLabelId = Long.parseLong(data.get("oneLabelId"));
        return courseService.getTwoLabelListByOneLabelId(oneLabelId);
    }

    // 通过二级标签id得到课程列表
    @GetMapping("/getCourseListByTwoLabelId")
    public ResultVO getCourseListByTwoLabelId(@RequestParam Map<String, String> data) {
        long formTypeId = Long.parseLong(data.get("formTypeId"));
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        
        return courseService.getCourseListByTwoLabelId(formTypeId, twoLabelId, currentPage, pageSize);
    }

    // 通过形式id和一级标签id, 二级标签id得到所有最热课程列表
    @GetMapping("/getFireCourseListByOneLabelAndTowLabelAndFormId")
    public R getFireCourseListByOneLabelAndTowLabelAndFormId(@RequestParam Map<String, String> data) {
        long formTypeId = Long.parseLong(data.get("formTypeId"));
        long oneLabelId = Long.parseLong(data.get("oneLabelId"));
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return courseService.getFireCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    // 通过形式id和一级标签id, 二级标签id得到所有最新课程列表
    @GetMapping("/getLastlyCourseListByOneLabelAndTowLabelAndFormId")
    public R getLastlyCourseListByOneLabelAndTowLabelAndFormId(@RequestParam Map<String, String> data) {
        long formTypeId = Long.parseLong(data.get("formTypeId"));
        long oneLabelId = Long.parseLong(data.get("oneLabelId"));
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return courseService.getLastlyCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    // 通过形式id和一级标签id, 二级标签id得到升序价格列表
    @GetMapping("/getAscPriceCourseListByOneLabelAndTowLabelAndFormId")
    public R getAscPriceCourseListByOneLabelAndTowLabelAndFormId(@RequestParam Map<String, String> data) {
        long formTypeId = Long.parseLong(data.get("formTypeId"));
        long oneLabelId = Long.parseLong(data.get("oneLabelId"));
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return courseService.getAscPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    // 通过形式id和一级标签id, 二级标签id得到降序价格列表
    @GetMapping("/getDescPriceCourseListByOneLabelAndTowLabelAndFormId")
    public R getDescPriceCourseListByOneLabelAndTowLabelAndFormId(@RequestParam Map<String, String> data) {
        long formTypeId = Long.parseLong(data.get("formTypeId"));
        long oneLabelId = Long.parseLong(data.get("oneLabelId"));
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return courseService.getDescPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    // 通过课程名模糊查询课程
    @GetMapping("/queryCourseByCourseTitle")
    public R queryCourseByCourseTitle(@RequestParam("title")String title,
                                      @RequestParam("currentPage")Integer currentPage,
                                      @RequestParam("pageSize")Integer pageSize) {
        return courseService.queryCourseByCourseTitle(title, currentPage, pageSize);
    }
}

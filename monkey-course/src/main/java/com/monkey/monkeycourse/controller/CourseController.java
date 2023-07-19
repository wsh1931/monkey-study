package com.monkey.monkeycourse.controller;

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

    // 通过二级标签id得到文章列表
    @GetMapping("/getCourseListByTwoLabelId")
    public ResultVO getCourseListByTwoLabelId(@RequestParam Map<String, String> data) {
        long twoLabelId = Long.parseLong(data.get("twoLabelId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return courseService.getCourseListByTwoLabelId(twoLabelId, currentPage, pageSize);
    }
}

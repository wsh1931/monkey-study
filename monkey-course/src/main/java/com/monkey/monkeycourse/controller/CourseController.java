package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeycourse.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/15 20:41
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程中心接口")
@RestController
@RequestMapping("/monkey-course/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @ApiOperation("得到形式类型集合")
    @GetMapping("/getFormTypeList")
    public R getFormTypeList() {
        return courseService.getFormTypeList();
    }

    @ApiOperation("得到一级标签列表")
    @GetMapping("/getOneLabelList")
    public ResultVO getOneLabelList() {
        return courseService.getOneLabelList();
    }

    @ApiOperation("通过一级标签id得到二级标签")
    @GetMapping("/getTwoLabelListByOneLabelId")
    public ResultVO getTwoLabelListByOneLabelId(@RequestParam("oneLabelId") @ApiParam("一级标签id")Long oneLabelId) {
        return courseService.getTwoLabelListByOneLabelId(oneLabelId);
    }

    @ApiOperation("通过二级标签id得到课程列表")
    @GetMapping("/getCourseListByTwoLabelId")
    public ResultVO getCourseListByTwoLabelId(@RequestParam("formTypeId") @ApiParam("形式类型id")Long formTypeId,
                                              @RequestParam("twoLabelId") @ApiParam("二级标签id")Long twoLabelId,
                                              @RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                              @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        
        return courseService.getCourseListByTwoLabelId(formTypeId, twoLabelId, currentPage, pageSize);
    }

    @ApiOperation("通过形式id和一级标签id, 二级标签id得到所有最热课程列表")
    @GetMapping("/getFireCourseListByOneLabelAndTowLabelAndFormId")
    public R getFireCourseListByOneLabelAndTowLabelAndFormId(@RequestParam("formTypeId") @ApiParam("形式类型id")Long formTypeId,
                                                             @RequestParam("oneLabelId") @ApiParam("一级标签id")Long oneLabelId,
                                                             @RequestParam("twoLabelId") @ApiParam("二级标签id")Long twoLabelId,
                                                             @RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return courseService.getFireCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    @ApiOperation("通过形式id和一级标签id, 二级标签id得到所有最新课程列表")
    @GetMapping("/getLastlyCourseListByOneLabelAndTowLabelAndFormId")
    public R getLastlyCourseListByOneLabelAndTowLabelAndFormId(@RequestParam("formTypeId") @ApiParam("形式类型id")Long formTypeId,
                                                               @RequestParam("oneLabelId") @ApiParam("一级标签id")Long oneLabelId,
                                                               @RequestParam("twoLabelId") @ApiParam("二级标签id")Long twoLabelId,
                                                               @RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                                               @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return courseService.getLastlyCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    @ApiOperation("通过形式id和一级标签id, 二级标签id得到升序价格列表")
    @GetMapping("/getAscPriceCourseListByOneLabelAndTowLabelAndFormId")
    public R getAscPriceCourseListByOneLabelAndTowLabelAndFormId(@RequestParam("formTypeId") @ApiParam("形式类型id")Long formTypeId,
                                                                 @RequestParam("oneLabelId") @ApiParam("一级标签id")Long oneLabelId,
                                                                 @RequestParam("twoLabelId") @ApiParam("二级标签id")Long twoLabelId,
                                                                 @RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                                                 @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return courseService.getAscPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    @ApiOperation("通过形式id和一级标签id, 二级标签id得到降序价格列表")
    @GetMapping("/getDescPriceCourseListByOneLabelAndTowLabelAndFormId")
    public R getDescPriceCourseListByOneLabelAndTowLabelAndFormId(@RequestParam("formTypeId") @ApiParam("形式类型id")Long formTypeId,
                                                                  @RequestParam("oneLabelId") @ApiParam("一级标签id")Long oneLabelId,
                                                                  @RequestParam("twoLabelId") @ApiParam("二级标签id")Long twoLabelId,
                                                                  @RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                                                  @RequestParam("pageSize") @ApiParam("每页数据量")Long pageSize) {
        return courseService.getDescPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
    }

    @ApiOperation("通过课程名模糊查询课程")
    @GetMapping("/queryCourseByCourseTitle")
    public R queryCourseByCourseTitle(@RequestParam("title")String title,
                                      @RequestParam("currentPage")Integer currentPage,
                                      @RequestParam("pageSize")Integer pageSize) {
        return courseService.queryCourseByCourseTitle(title, currentPage, pageSize);
    }
}

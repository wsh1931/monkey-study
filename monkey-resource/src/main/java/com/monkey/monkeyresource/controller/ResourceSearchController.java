package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourceSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.swing.plaf.PanelUI;

/**
 * @author: wusihao
 * @date: 2023/10/14 14:00
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源搜索中心")
@RestController
@RequestMapping("/monkey-resource/search")
public class ResourceSearchController {
    @Resource
    private ResourceSearchService resourceSearchService;

    @ApiOperation("查询形式类型列表")
    @GetMapping("/queryFormTypeList")
    public R queryFormTypeList() {
        return resourceSearchService.queryFormTypeList();
    }

    @ApiOperation("得到方向集合(包括全部)")
    @GetMapping("/queryDirectList")
    public R queryDirectList() {
        return resourceSearchService.queryDirectList();
    }

    @ApiOperation("通过方向集合查询分类集合")
    @GetMapping("/queryClassificationByDirectId")
    public R queryClassificationByDirectId(@RequestParam("directId") @ApiParam("方向id") Long directId) {
        return resourceSearchService.queryClassificationByDirectId(directId);
    }

    @ApiOperation("查询格式列表")
    @GetMapping("/queryFormatList")
    public R queryFormatList() {
        return resourceSearchService.queryFormatList();
    }

    @ApiOperation("查找最热资源列表")
    @GetMapping("/queryHottestResourceList")
    public R queryHottestResourceList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                      @RequestParam(value = "formTypeId") @ApiParam("形式类型id") Long formTypeId,
                                      @RequestParam(value = "directionId") @ApiParam("方向id") Long directionId,
                                      @RequestParam(value = "classificationId", required = false) @ApiParam("分类id") Long classificationId,
                                      @RequestParam(value = "format") @ApiParam("格式") String format,
                                      @RequestParam(value = "resourceName", required = false) @ApiParam("模糊查询资源名") String resourceName) {
        return resourceSearchService.queryHottestResourceList(currentPage, pageSize, formTypeId, directionId, classificationId, format, resourceName);
    }

    @ApiOperation("查找最新资源列表")
    @GetMapping("/queryLatestResourceList")
    public R queryLatestResourceList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                      @RequestParam(value = "formTypeId") @ApiParam("形式类型id") Long formTypeId,
                                      @RequestParam(value = "directionId") @ApiParam("方向id") Long directionId,
                                      @RequestParam(value = "classificationId", required = false) @ApiParam("分类id") Long classificationId,
                                      @RequestParam(value = "format") @ApiParam("格式") String format,
                                     @RequestParam(value = "resourceName", required = false) @ApiParam("模糊查询资源名") String resourceName) {
        return resourceSearchService.queryLatestResourceList(currentPage, pageSize, formTypeId, directionId, classificationId, format, resourceName);
    }

    @ApiOperation("得到升序价格列表")
    @GetMapping("/queryAscPriceResourceList")
    public R queryAscPriceResourceList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                     @RequestParam(value = "formTypeId") @ApiParam("形式类型id") Long formTypeId,
                                     @RequestParam(value = "directionId") @ApiParam("方向id") Long directionId,
                                     @RequestParam(value = "classificationId", required = false) @ApiParam("分类id") Long classificationId,
                                     @RequestParam(value = "format") @ApiParam("格式") String format,
                                       @RequestParam(value = "resourceName", required = false) @ApiParam("模糊查询资源名") String resourceName) {
        return resourceSearchService.queryAscPriceResourceList(currentPage, pageSize, formTypeId, directionId, classificationId, format, resourceName);
    }

    @ApiOperation("得到降序价格列表")
    @GetMapping("/queryDescPriceResourceList")
    public R queryDescPriceResourceList(@RequestParam("currentPage") @ApiParam("当前页")Long currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam(value = "formTypeId") @ApiParam("形式类型id") Long formTypeId,
                                       @RequestParam(value = "directionId") @ApiParam("方向id") Long directionId,
                                       @RequestParam(value = "classificationId", required = false) @ApiParam("分类id") Long classificationId,
                                       @RequestParam(value = "format") @ApiParam("格式") String format,
                                        @RequestParam(value = "resourceName", required = false) @ApiParam("模糊查询资源名") String resourceName) {
        return resourceSearchService.queryDescPriceResourceList(currentPage, pageSize, formTypeId, directionId, classificationId, format, resourceName);
    }
}

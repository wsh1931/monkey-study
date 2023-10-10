package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourceClassificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/10 9:15
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源分类接口")
@RestController
@RequestMapping("/monkey-resource/classification")
public class ResourceClassificationController {
    @Resource
    private ResourceClassificationService resourceClassificationService;

    @ApiOperation("得到一级标签")
    @GetMapping("/queryOneLevelClassificationList")
    public R queryOneLevelClassificationList() {
        return resourceClassificationService.queryOneLevelClassificationList();
    }

    @ApiOperation("通过搜索字段得到一级标签")
    @GetMapping("/queryOneClassification")
    public R queryOneClassification(@RequestParam("search") @ApiParam("一级分类搜索字段") String oneClassificationName) {
        return resourceClassificationService.queryOneClassification(oneClassificationName);
    }

    @ApiOperation("通过一级分类id得到二级分类列表")
    @GetMapping("/queryTwoClassificationList/by/classificationOneId")
    public R queryTwoClassificationListByOneLabelId(@RequestParam("classificationOneId") @ApiParam("一级分类搜索字段") Long classificationOneId) {
        return resourceClassificationService.queryTwoClassificationListByOneLabelId(classificationOneId);
    }
}

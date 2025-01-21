package com.monkey.monkeyresource.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.ResourceConditionVo;
import com.monkey.monkeyresource.service.ResourceContentManageService;
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
 * @date: 2023/12/19 15:07
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源内容管理接口")
@RestController
@RequestMapping("/monkey-resource/content/manage")
public class ResourceContentManageController {
    @Resource
    private ResourceContentManageService resourceContentManageService;

    @ApiOperation("查询资源类型集合")
    @GetMapping("/queryResourceType")
    public R queryResourceType() {
        return resourceContentManageService.queryResourceType();
    }

    @ApiOperation("查询形式类型集合")
    @GetMapping("/queryFormType")
    public R queryFormType() {
        return resourceContentManageService.queryFormType();
    }

    @ApiOperation("通过条件查询资源列表")
    @GetMapping("/queryResourceByCondition")
    public R queryResourceByCondition(@RequestParam("conditionStr") @ApiParam("条件字符串")String conditionStr) {
        ResourceConditionVo resourceConditionVo = JSONObject.parseObject(conditionStr, ResourceConditionVo.class);
        return resourceContentManageService.queryResourceByCondition(resourceConditionVo);
    }

    @ApiOperation("查询资源")
    @GetMapping("/queryResource")
    public R queryResource(@RequestParam("conditionStr") @ApiParam("条件字符串")String conditionStr) {
        ResourceConditionVo resourceConditionVo = JSONObject.parseObject(conditionStr, ResourceConditionVo.class);
        return resourceContentManageService.queryResource(resourceConditionVo);
    }

    @ApiOperation("查询资源近一周的资源数据")
    @GetMapping("/queryResourceDataRecentWeek")
    public R queryResourceDataRecentWeek(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceContentManageService.queryResourceDataRecentWeek(resourceId);
    }
}

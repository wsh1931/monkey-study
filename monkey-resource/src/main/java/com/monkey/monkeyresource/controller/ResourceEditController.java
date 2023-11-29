package com.monkey.monkeyresource.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.service.ResourceEditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/28 17:28
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源编辑接口")
@RestController
@RequestMapping("/monkey-resource/edit")
public class ResourceEditController {
    @Resource
    private ResourceEditService resourceEditService;

    @ApiOperation("通过资源id得到资源信息")
    @GetMapping("/queryResourceById")
    public R queryResourceById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceEditService.queryResourceById(resourceId);
    }

    @ApiOperation("更新资源")
    @PutMapping("/updateResource")
    @PreAuthorize("@commonAuthority.isSameUser(#userId)")
    public R updateResource(@RequestParam("resourceVoStr") @ApiParam("上传资源字符串") String resourceVoStr,
                            @RequestParam("userId") @ApiParam("资源作者id") Long userId) {
        UploadResourcesVo uploadResourcesVo = JSONObject.parseObject(resourceVoStr, UploadResourcesVo.class);
        return resourceEditService.updateResource(uploadResourcesVo);
    }
}

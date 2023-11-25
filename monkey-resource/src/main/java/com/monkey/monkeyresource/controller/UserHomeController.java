package com.monkey.monkeyresource.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.service.UserHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/23 22:37
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页资源接口")
@RestController
@RequestMapping("/monkey-resource/user/home")
public class UserHomeController {
    @Resource
    private UserHomeService userHomeService;

    @ApiOperation("通过用户id查询资源集合")
    @GetMapping("/queryResourceByUserId")
    public R queryResourceByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                   @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeService.queryResourceByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("通过资源id得到资源信息")
    @GetMapping("/queryResourceById")
    public R queryResourceById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return userHomeService.queryResourceById(resourceId);
    }

    @ApiOperation("更新资源")
    @PutMapping("/updateResource")
    @PreAuthorize("@commonAuthority.isSameUser(#userId)")
    public R updateResource(@RequestParam("resourceVoStr") @ApiParam("上传资源字符串") String resourceVoStr,
                            @RequestParam("userId") @ApiParam("资源作者id") Long userId) {
        UploadResourcesVo uploadResourcesVo = JSONObject.parseObject(resourceVoStr, UploadResourcesVo.class);
        return userHomeService.updateResource(uploadResourcesVo);
    }

    @ApiOperation("删除资源")
    @DeleteMapping("/deleteResource")
    @PreAuthorize("@commonAuthority.isSameUser(#userId)")
    public R deleteResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                            @RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userHomeService.deleteResource(resourceId);
    }
}
package com.monkey.monkeyresource.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.service.UserHomeResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
public class UserHomeResourceController {
    @Resource
    private UserHomeResourceService userHomeResourceService;

    @ApiOperation("通过用户id查询资源集合")
    @GetMapping("/queryResourceByUserId")
    public R queryResourceByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                   @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeResourceService.queryResourceByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("删除资源")
    @DeleteMapping("/deleteResource")
    @PreAuthorize("@commonAuthority.isSameUser(#userId)")
    public R deleteResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                            @RequestParam("userId") @ApiParam("用户id") Long userId) {
        return userHomeResourceService.deleteResource(resourceId);
    }
}
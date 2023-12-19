package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourceDetailService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: wusihao
 * @date: 2023/10/16 8:59
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源详情接口")
@RestController
@RequestMapping("/monkey-resource/detail")
public class ResourceDetailController {
    @Resource
    private ResourceDetailService resourceDetailService;

    @ApiOperation("查询资源信息")
    @GetMapping("/queryResourceInfo")
    public R queryResourceInfo(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceDetailService.queryResourceInfo(resourceId);
    }

    @ApiOperation("下载文件资源")
    @PostMapping("/downFileResource")
    public void downFileResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                 HttpServletResponse response,
                                 HttpServletRequest request) {

        resourceDetailService.downFileResource(response, request, resourceId);
    }

    @ApiOperation("查询资源评价信息")
    @GetMapping("/queryResourceEvaluateInfo")
    public R queryResourceEvaluateInfo(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceDetailService.queryResourceEvaluateInfo(resourceId);
    }

    @ApiOperation("查询相关资源列表")
    @GetMapping("/queryRelateResourceList")
    public R resourceEvaluateInfo(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceDetailService.resourceEvaluateInfo(resourceId);
    }

    @ApiOperation("判断用户是否点赞或收藏此资源")
    @GetMapping("/judgeUserIsLikeOrCollectResource")
    public R judgeUserIsLikeOrCollectResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        String userId = JwtUtil.getUserId();
        return resourceDetailService.judgeUserIsLikeOrCollectResource(userId, resourceId);
    }

    @ApiOperation("点赞资源")
    @PostMapping("/likeResource")
    public R likeResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                          @RequestParam("recipientId") @ApiParam("接收者id") Long recipientId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceDetailService.likeResource(userId, resourceId, recipientId);
    }

    @ApiOperation("取消点赞资源")
    @PostMapping("/cancelLikeResource")
    public R cancelLikeResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                @RequestParam("authorId") @ApiParam("作者id") Long authorId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceDetailService.cancelLikeResource(userId, resourceId, authorId);
    }


    @ApiOperation("精选资源")
    @PutMapping("/curationResource")
    public R curationResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceDetailService.curationResource(userId, resourceId);
    }

    @ApiOperation("取消精选资源")
    @PutMapping("/cancelCurationResource")
    public R cancelCurationResource(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourceDetailService.cancelCurationResource(userId, resourceId);
    }

    @ApiOperation("判断资源是否存在")
    @GetMapping("/judgeResourceIsExist")
    public R judgeResourceIsExist(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceDetailService.judgeResourceIsExist(resourceId);
    }
}

package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.service.ResourceDetailService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;

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
}

package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.UploadResourceService;
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
 * @date: 2023/10/7 17:46
 * @version: 1.0
 * @description:
 */
@Api(tags = "上传资源")
@RestController
@RequestMapping("/monkey-resource/uploadResource")
public class UploadResourceController {
    @Resource
    private UploadResourceService uploadResourceService;

    @ApiOperation("通过文件类型得到文件类型图片")
    @GetMapping("/queryFileTypeIcon")
    public R queryFileTypeIcon(@RequestParam("fileType") @ApiParam("文件类型") String fileType) {
        return uploadResourceService.queryFileTypeIcon(fileType);
    }
}

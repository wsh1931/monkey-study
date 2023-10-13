package com.monkey.monkeyresource.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.service.UploadResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("上传资源")
    @PostMapping("/uploadResource")
    public R uploadResource(@RequestParam("resourceVoStr") @ApiParam("提交资源表单字符串") String resourceVoStr) {
        UploadResourcesVo uploadResourcesVo = JSONObject.parseObject(resourceVoStr, UploadResourcesVo.class);
        return R.ok(uploadResourceService.uploadResource(uploadResourcesVo));
    }
}

package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.LabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "文章标签接口")
@RestController
@RequestMapping("/monkey-article/blog/label")
public class LabelController {
    @Resource
    private LabelService labelService;

    @ApiOperation("得到标签列表")
    @GetMapping("/getLabelList")
    public ResultVO getLabelList() {
        return labelService.getLabelList();
    }
}

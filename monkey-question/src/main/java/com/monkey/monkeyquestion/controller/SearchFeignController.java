package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.service.SearchFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/9 16:20
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜索模块调用问答模块接口")
@RestController
@RequestMapping("/monkey-question/search/feign")
public class SearchFeignController {
    @Resource
    private SearchFeignService searchFeignService;

    @ApiOperation("查询所有问答")
    @GetMapping("/queryAllQuestion")
    public R queryAllQuestion() {
        return searchFeignService.queryAllQuestion();
    }

    @ApiOperation("得到所有用户所有问答，点赞，收藏，游览数")
    @GetMapping("/queryAllUserQuestionInfo")
    public R queryAllUserQuestionInfo() {
        return searchFeignService.queryAllUserQuestionInfo();
    }
}

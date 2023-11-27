package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.ESQuestionService;
import com.monkey.monkeysearch.service.ESQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/9 16:08
 * @version: 1.0
 * @description:
 */
@Api(tags = "elasticsearch问答搜索接口")
@RestController
@RequestMapping("/monkey-search/question")
public class ESQuestionController {
    @Resource
    private ESQuestionService esQuestionService;

    @ApiOperation("查询所有问答文档")
    @GetMapping("/queryAllQuestionDocument")
    public R queryAllQuestionDocument() {
        return esQuestionService.queryAllQuestionDocument();
    }

    @ApiOperation("删除所有问答文档")
    @DeleteMapping("/deleteAllQuestionDocument")
    public R deleteAllQuestionDocument() {
        return esQuestionService.deleteAllQuestionDocument();
    }

    @ApiOperation("将问答数据库中所有数据存入elasticsearch问答文档中")
    @PostMapping("/insertQuestionDocument")
    public R insertQuestionDocument() {
        return esQuestionService.insertQuestionDocument();
    }

    @ApiOperation("查询综合问答列表")
    @GetMapping("/queryComprehensiveQuestion")
    public R queryComprehensiveQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                       @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryComprehensiveQuestion(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询回复最多问答列表")
    @GetMapping("/queryReplyQuestion")
    public R queryReplyQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                               @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryReplyQuestion(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询收藏数最多问答列表")
    @GetMapping("/queryCollectQuestion")
    public R queryCollectQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryCollectQuestion(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询点赞数最多问答列表")
    @GetMapping("/queryLikeQuestion")
    public R queryLikeQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryLikeQuestion(currentPage, pageSize, keyword);
    }
    @ApiOperation("查询游览数最多问答列表")
    @GetMapping("/queryViewQuestion")
    public R queryViewQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryViewQuestion(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最热问答列表")
    @GetMapping("/queryHireQuestion")
    public R queryHireQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                              @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryHireQuestion(currentPage, pageSize, keyword);
    }

    @ApiOperation("查询最新问答列表")
    @GetMapping("/queryLatestQuestion")
    public R queryLatestQuestion(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                @RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        return esQuestionService.queryLatestQuestion(currentPage, pageSize, keyword);
    }
}

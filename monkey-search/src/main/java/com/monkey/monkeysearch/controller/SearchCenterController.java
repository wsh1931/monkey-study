package com.monkey.monkeysearch.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.service.SearchCenterService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/17 20:23
 * @version: 1.0
 * @description:
 */
@Api(tags = "搜索中心接口")
@RestController
@RequestMapping("/monkey-search/search/center")
public class SearchCenterController {
    @Resource
    private SearchCenterService searchCenterService;

    @ApiOperation("查找相关搜索")
    @GetMapping("/queryRelatedSearch")
    public R queryRelatedSearch(@RequestParam("keyword") @ApiParam("搜索关键字") String keyWord,
                                @RequestParam("searchType") @ApiParam("搜索类型") Integer searchType) {
        return searchCenterService.queryRelatedSearch(keyWord, searchType);
    }

    @ApiOperation("查询该登录用户历史搜索信息")
    @GetMapping("/queryHistorySearch")
    public R queryHistorySearch() {
        String userId = JwtUtil.getUserId();
        return searchCenterService.queryHistorySearch(userId);
    }

    @ApiOperation("将搜索信息插入历史搜索")
    @PostMapping("/insertHistorySearch")
    public R insertHistorySearch(@RequestParam("keyword") @ApiParam("搜索关键字") String keyword) {
        String userId = JwtUtil.getUserId();
        return searchCenterService.insertHistorySearch(userId, keyword);
    }
}

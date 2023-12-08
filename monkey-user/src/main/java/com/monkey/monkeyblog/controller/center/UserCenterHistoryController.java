package com.monkey.monkeyblog.controller.center;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.center.UserCenterHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/7 16:24
 * @version: 1.0
 * @description:
 */
@Api(tags = "个人中心-历史游览接口")
@RestController
@RequestMapping("/monkey-user/center/history")
public class UserCenterHistoryController {
    @Resource
    private UserCenterHistoryService userCenterHistoryService;

    @ApiOperation("查询历史内容集合")
    @GetMapping("/queryHistoryContent")
    public R queryHistoryContent(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userCenterHistoryService.queryHistoryContent(currentPage, pageSize);
    }

    @ApiOperation("清除用户历史内容")
    @DeleteMapping("/clearHistoryContent")
    public R clearHistoryContent() {
        return userCenterHistoryService.clearHistoryContent();
    }
}

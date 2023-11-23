package com.monkey.monkeyblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserCollectService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/7/31 17:07
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户收藏接口")
@RestController
@RequestMapping("/monkey-user/collect")
public class UserCollectController {
    @Resource
    private UserCollectService userCollectService;

    @ApiOperation("通过用户id得到用户收藏目录")
    @GetMapping("/getCollectContentListByUserId")
    public R getCollectContentListByUserId(@RequestParam("associateId") @ApiParam("关联id")Long associateId,
                                           @RequestParam("collectType") @ApiParam("收藏类型")Integer collectType) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return userCollectService.getCollectContentListByUserId(userId, associateId, collectType);
    }

    @ApiOperation("创建收藏夹")
    @PostMapping("/create/content")
    public R createContent(@RequestParam("content") @ApiParam("文件夹实习类字符串")String contentStr) {
        CollectContent content = JSONObject.parseObject(contentStr, CollectContent.class);
        return userCollectService.createContent(content);
    }

    @ApiOperation("收藏功能实现")
    @PostMapping("/collectContent")
    public R collectContent(@RequestParam("associateId") @ApiParam("关联id")Long associateId,
                            @RequestParam("collectType") @ApiParam("收藏类型")Integer collectType,
                            @RequestParam("collectTitle") @ApiParam("收藏夹标题")String collectTitle,
                            @RequestParam("collectContentId") @ApiParam("收藏目录id")Long collectContentId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return userCollectService.collectContent(collectContentId, associateId, collectType, collectTitle, userId);
    }
}

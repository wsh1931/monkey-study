package com.monkey.monkeycommunity.controller.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.manage.InfoManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/6 14:43
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区信息管理")
@RestController
@RequestMapping("/monkey-community/manage/infoManage")
public class InfoManageController {
    @Resource
    private InfoManageService infoManageService;

    @ApiOperation("通过社区id查询社区基本信息")
    @GetMapping("/queryCommunityInfo")
    public R queryCommunityInfo(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return infoManageService.queryCommunityInfo(communityId);
    }

    @ApiOperation("更新社区信息")
    @PutMapping("/updateCommunityInfo")
    public R updateCommunityInfo(@RequestParam("communityVoStr") @ApiParam("社区Vo字符串") String communityVoStr) {
        return infoManageService.updateCommunityInfo(communityVoStr);
    }

    @ApiOperation("更新社区公告")
    @PutMapping("/updateCommunityNotice")
    public R updateCommunityNotice(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                   @RequestParam("communityNotice") @ApiParam("社区通知") String communityNotice) {
        return infoManageService.updateCommunityNotice(communityId, communityNotice);
    }
}

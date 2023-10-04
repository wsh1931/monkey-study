package com.monkey.monkeycommunity.controller.manage;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityUserApplication;
import com.monkey.monkeycommunity.service.manage.UserApplicationService;
import com.monkey.spring_security.JwtUtil;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/3 14:29
 * @version: 1.0
 * @description:
 */
@Api(tags = "加入申请")
@RestController
@RequestMapping("/monkey-community/manage/userApplication")
public class UserApplicationController {
    @Resource
    private UserApplicationService userApplicationService;

    @ApiOperation("查询用户申请列表")
    @GetMapping("/queryUserApplicationList")
    public R queryUserApplicationList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                      @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userApplicationService.queryUserApplicationList(communityId, currentPage, pageSize);
    }


    @ApiOperation("批量拒绝用户加入社区")
    @PostMapping("/batchRefuse/userApplication")
    public R batchRefuseUserApplication(@RequestParam("communityUserApplicationStrList") @ApiParam("社区用户申请集合字符串") String communityUserApplicationStrList) {
        List<CommunityUserApplication> communityUserApplicationList = JSONObject.parseArray(communityUserApplicationStrList, CommunityUserApplication.class);
        long nowUserId = Long.parseLong(JwtUtil.getUserId());
        return userApplicationService.batchRefuseUserApplication(communityUserApplicationList, nowUserId);
    }

    @ApiOperation("批量通过用户申请")
    @PostMapping("/batchPass/userApplication")
    public R batchPassUserApplication(@RequestParam("communityUserApplicationStrList") @ApiParam("社区用户申请集合字符串") String communityUserApplicationStrList) {
        List<CommunityUserApplication> communityUserApplicationList = JSONObject.parseArray(communityUserApplicationStrList, CommunityUserApplication.class);
        long nowUserId = Long.parseLong(JwtUtil.getUserId());
        return userApplicationService.batchPassUserApplication(nowUserId, communityUserApplicationList);
    }

    @ApiOperation("查询已通过申请列表")
    @GetMapping("/querySuccessApplicationList")
    public R querySuccessApplicationList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                       @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                       @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userApplicationService.querySuccessApplicationList(communityId, currentPage, pageSize);
    }

    @ApiOperation("查询已拒绝申请列表")
    @GetMapping("/queryRefuseApplicationList")
    public R queryRefuseApplicationList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                        @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                        @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userApplicationService.queryRefuseApplicationList(communityId, currentPage, pageSize);
    }

    @ApiOperation("批量删除申请记录")
    @DeleteMapping("/batchDelete/userApplication")
    public R batchDeleteUserApplication(@RequestParam("communityUserApplicationStrList") @ApiParam("社区用户申请集合字符串") String communityUserApplicationStrList) {
        List<CommunityUserApplication> communityUserApplicationList = JSONObject.parseArray(communityUserApplicationStrList, CommunityUserApplication.class);
        return userApplicationService.batchDeleteUserApplication(communityUserApplicationList);
    }

    @ApiOperation("删除全部拒绝用户申请记录")
    @DeleteMapping("/deleteAllRefuseRecord")
    public R deleteAllRefuseRecord(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userApplicationService.deleteAllRefuseRecord(communityId);
    }

    @ApiOperation("删除全部已通过用户申请记录")
    @DeleteMapping("/deleteAllSuccessRecord")
    public R deleteAllSuccessRecord(@RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        return userApplicationService.deleteAllSuccessRecord(communityId);
    }
}

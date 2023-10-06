package com.monkey.monkeycommunity.controller.manage;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityChannel;
import com.monkey.monkeycommunity.service.manage.ChannelManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.swing.plaf.PanelUI;

/**
 * @author: wusihao
 * @date: 2023/10/5 16:02
 * @version: 1.0
 * @description:
 */
@Api(tags = "频道管理")
@RestController
@RequestMapping("/monkey-community/manage/channelManage")
public class ChannelManageController {
    @Resource
    private ChannelManageService channelManageService;

    @ApiOperation("查询社区频道管理列表")
    @GetMapping("/queryChannelManageList")
    public R queryChannelManageList(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                    @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                    @RequestParam("channelName") @ApiParam("模糊查询的频道名称") String channelName) {
        return channelManageService.queryChannelManageList(communityId, currentPage, pageSize, channelName);
    }

    @ApiOperation("更新是否支持前端展示")
    @PutMapping("/updateSupportShow")
    public R updateSupportShow(@RequestParam("channelId") @ApiParam("频道id")Long channelId,
                               @RequestParam("supportShow") @ApiParam("是否支持前端展示字段") Integer supportShow) {
        return channelManageService.updateSupportShow(channelId, supportShow);
    }
    @ApiOperation("更新是否支持用户发表文章")
    @PutMapping("/updateSupportUserPublish")
    public R updateSupportUserPublish(@RequestParam("channelId") @ApiParam("频道id")Long channelId,
                               @RequestParam("supportUserPublish") @ApiParam("是否支持用户发表字段") Integer supportUserPublish) {
        return channelManageService.updateSupportUserPublish(channelId, supportUserPublish);
    }
    @ApiOperation("更新是否支持管理员修改")
    @PutMapping("/updateSupportManageModify")
    public R updateSupportManageModify(@RequestParam("channelId") @ApiParam("频道id")Long channelId,
                               @RequestParam("supportManageModify") @ApiParam("是否支持管理员修改字段") Integer supportManageModify) {
        return channelManageService.updateSupportManageModify(channelId, supportManageModify);
    }

    @ApiOperation("提交频道编辑")
    @PutMapping("/submitChannelEdit")
    public R submitChannelEdit(@RequestParam("communityChannelStr") @ApiParam("社区频道字符串") String communityChannelStr) {
        CommunityChannel communityChannel = JSONObject.parseObject(communityChannelStr, CommunityChannel.class);
        return channelManageService.submitChannelEdit(communityChannel);
    }

    @ApiOperation("删除频道")
    @DeleteMapping("/deleteChannel")
    public R deleteChannel(@RequestParam("channelId") @ApiParam("频道id") Long channelId) {
        return channelManageService.deleteChannel(channelId);
    }

    @ApiOperation("新增频道")
    @PostMapping("/addChannel")
    public R addChannel(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                        @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                        @RequestParam("sort") @ApiParam("排序字段") Long sort) {
        return channelManageService.addChannel(communityId, channelName, sort);
    }
}

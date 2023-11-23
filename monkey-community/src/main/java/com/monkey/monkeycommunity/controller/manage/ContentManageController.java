package com.monkey.monkeycommunity.controller.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.manage.ContentManageService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/5 9:13
 * @version: 1.0
 * @description:
 */
@Api(tags = "内容管理")
@RestController
@RequestMapping("/monkey-community/manage/contentManage")
public class ContentManageController {
    @Resource
    private ContentManageService contentManageService;

    @ApiOperation("通过条件查询内容管理集合")
    @GetMapping("/queryContentManageList/byCondition")
    @PreAuthorize("@communityCustomAuthority.communityManageAuthority" +
            "(T(com.monkey.monkeyUtils.constants.CommunityManageMenuEnum).CONTENT_MANAGE.perms + #communityId)")
    public R queryContentManageListByCondition(@RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                               @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                               @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize,
                                               @RequestParam("status") @ApiParam("状态条件") String status,
                                               @RequestParam("channel") @ApiParam("频道信息（频道id,频道名称）以.分隔条件") String channel,
                                               @RequestParam("publisherId") @ApiParam("发布者条件") String publisherId) {
        long nowUserId = Long.parseLong(JwtUtil.getUserId());
        return contentManageService.queryContentManageListByCondition(nowUserId, communityId, status, channel, publisherId, currentPage, pageSize);
    }

    @ApiOperation("判断社区文章是否存在")
    @GetMapping("/judgeCommunityArticleIsExist")
    public R judgeCommunityArticleIsExist(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        return contentManageService.judgeCommunityArticleIsExist(communityArticleId);
    }
}

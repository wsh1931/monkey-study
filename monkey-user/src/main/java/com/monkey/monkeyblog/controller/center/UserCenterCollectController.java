package com.monkey.monkeyblog.controller.center;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.center.UserCenterCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/5 15:07
 * @version: 1.0
 * @description:
 */
@Api(tags = "个人中心-收藏功能接口")
@RestController
@RequestMapping("/monkey-user/center/collect")
public class UserCenterCollectController {
    @Resource
    private UserCenterCollectService userCenterCollectService;

    @ApiOperation("查询收藏目录以及对应的收藏数")
    @GetMapping("/queryCollectContent")
    public R queryCollectContent(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userCenterCollectService.queryCollectContent(currentPage, pageSize);
    }

    @ApiOperation("查询收藏目录信息通过收藏目录id")
    @GetMapping("/queryCollectContentById")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R queryCollectContentById(@RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.queryCollectContentById(collectContentId);
    }

    @ApiOperation("查询收藏目录关系列表")
    @GetMapping("/queryContentConnect")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R queryContentConnect(@RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId,
                                 @RequestParam("type") @ApiParam("查找的内容类型") Integer type,
                                 @RequestParam("keyword") @ApiParam("搜索关键字") String keyword,
                                 @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userCenterCollectService.queryContentConnect(collectContentId, type, keyword, currentPage, pageSize);
    }

    @ApiOperation("更新收藏目录描述")
    @GetMapping("/updateCollectDescription")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R updateCollectDescription(@RequestParam("description") @ApiParam("收藏描述") String description,
                                      @RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.updateCollectDescription(collectContentId, description);
    }

    @ApiOperation("更新收藏目录标题")
    @GetMapping("/updateCollectName")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R updateCollectName(@RequestParam("name") @ApiParam("收藏名称") String name,
                                      @RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.updateCollectName(collectContentId, name);
    }

    @ApiOperation("删除收藏夹")
    @DeleteMapping("/deleteCollectContent")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R deleteCollectContent(@RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.deleteCollectContent(collectContentId);
    }

    @ApiOperation("设置收藏夹为私密收藏夹")
    @PutMapping("/setCollectPrivate")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R setCollectPrivate(@RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.setCollectPrivate(collectContentId);
    }

    @ApiOperation("设置收藏夹为公开收藏夹")
    @PutMapping("/setCollectPublic")
    @PreAuthorize("@userCustomAuthority.judgeIsAuthorByCollectContentId(#collectContentId)")
    public R setCollectPublic(@RequestParam("collectContentId") @ApiParam("收藏目录id") Long collectContentId) {
        return userCenterCollectService.setCollectPublic(collectContentId);
    }
}

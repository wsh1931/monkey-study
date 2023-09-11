package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityDetailService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/9 17:17
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区详情页面")
@RestController
@RequestMapping("/monkey-community/community/detail")
public class CommunityDetailController {
    @Resource
    private CommunityDetailService communityDetailService;

    @ApiOperation("得到我加入的社区数量")
    @GetMapping("/queryMyAddCommunityCount")
    public R queryMyAddCommunityCount() {
        String userId = JwtUtil.getUserId();
        return communityDetailService.queryMyAddCommunityCount(userId);
    }

    @ApiOperation("得到我管理的社区数量")
    @GetMapping("/queryMyManageCommunityCount")
    public R queryMyManageCommunityCount() {
        String userId = JwtUtil.getUserId();
        return communityDetailService.queryMyManageCommunityCount(userId);
    }

    @ApiOperation("得到官方推荐的社区数量")
    @GetMapping("/queryRecommendCommunityCount")
    public R queryRecommendCommunityCount() {
        return communityDetailService.queryRecommendCommunityCount();
    }

    @ApiOperation("得到其他社区数")
    @GetMapping("/queryOtherCommunityCount")
    public R queryOtherCommunityCount() {
        return communityDetailService.queryOtherCommunityCount();
    }

    @ApiOperation("查询我加入的社区集合")
    @GetMapping("/queryMyAddCommunityList")
    public R queryMyAddCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityDetailService.queryMyAddCommunityList(userId);
    }

    @ApiOperation("查询我管理的社区集合")
    @GetMapping("/queryMyManegeCommunityList")
    public R queryMyManegeCommunityList() {
        String userId = JwtUtil.getUserId();
        return communityDetailService.queryMyManegeCommunityList(userId);
    }

    @ApiOperation("查询推荐社区集合")
    @GetMapping("/queryRecommendCommunityList")
    public R queryRecommendCommunityList() {
        return communityDetailService.queryRecommendCommunityList();
    }

    @ApiOperation("查询其他社区集合")
    @GetMapping("/queryOtherCommunityListList")
    public R queryOtherCommunityListList() {
        return communityDetailService.queryOtherCommunityListList();
    }

    @ApiOperation("通过社区名模糊搜索社区信息")
    @GetMapping("/searchCommunityByCommunityName")
    public R searchCommunityByCommunityName(@RequestParam("communityName") @ApiParam("社区名") String communityName) {
        return communityDetailService.searchCommunityByCommunityName(communityName);
    }

    @ApiOperation("得到最新文章列表")
    @GetMapping("/queryLatestArticleList/ByChannelId/CommunityId")
    public R queryLatestArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                             @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                             @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryLatestArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到热文章列表")
    @GetMapping("/queryHottestArticleList/ByChannelId/CommunityId")
    public R queryHottestArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                              @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                              @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryHottestArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到评分降序列表")
    @GetMapping("/queryScoreArticleList/ByChannelId/CommunityId")
    public R queryScoreArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                            @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                            @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryScoreArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到阅读量文章列表")
    @GetMapping("/queryViewsArticleList/ByChannelId/CommunityId")
    public R queryViewsArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                            @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                            @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryViewsArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到精选文章列表")
    @GetMapping("/queryExcellentArticleList/ByChannelId/CommunityId")
    public R queryExcellentArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                                @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                                @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryExcellentArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到置顶文章列表")
    @GetMapping("/queryTopArticleList/ByChannelId/CommunityId")
    public R queryTopArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                          @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                          @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryTopArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到点赞排序文章列表")
    @GetMapping("/queryLikeArticleList/ByChannelId/CommunityId")
    public R queryLikeArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                           @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                           @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryLikeArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到收藏排序文章列表")
    @GetMapping("/queryCollectArticleList/ByChannelId/CommunityId")
    public R queryCollectArticleListByChannelIdAndCommunityId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                              @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                              @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        return communityDetailService.queryCollectArticleListByChannelIdAndCommunityId(currentPage, pageSize, communityId, channelName, channelId);
    }

    @ApiOperation("得到我的文章列表")
    @GetMapping("/queryWithMetArticleList/ByChannelId/CommunityId")
    public R queryWithMeCommunityListByCommunityIdAndChannelId(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                             @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                                             @RequestParam("communityId") @ApiParam("社区id") Long communityId,
                                                               @RequestParam("channelName") @ApiParam("频道名称") String channelName,
                                                               @RequestParam("channelId") @ApiParam("频道id") String channelId) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return communityDetailService.queryWithMeCommunityListByCommunityIdAndChannelId(currentPage, pageSize, communityId, channelName, userId, channelId);
    }

    @ApiOperation("通过搜索字段模糊搜索文章标题")
    @GetMapping("/searchArticleContent")
    public R searchArticleContent(@RequestParam("title") @ApiParam("模糊搜索字段") String title,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        return communityDetailService.searchArticleContent(title, currentPage, pageSize);
    }
}

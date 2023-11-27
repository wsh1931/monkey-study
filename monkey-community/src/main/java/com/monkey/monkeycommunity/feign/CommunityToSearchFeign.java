package com.monkey.monkeycommunity.feign;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.Community;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "community-to-search")
public interface CommunityToSearchFeign {
    // 社区文章游览数 + 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleViewAddOne")
    R communityArticleViewAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    // 社区文章评论数 + 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleCommentCountAdd")
    R communityArticleCommentCountAdd(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    @PutMapping("/monkey-search/community/article/feign/communityArticleCommentSub")
    R communityArticleCommentSub(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                       @RequestParam("sum") @ApiParam("减去的数目") Long sum);

    // 社区文章收藏数 + 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleCollectCountAddOne")
    R communityArticleCollectCountAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    // 社区文章收藏数 - 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleCollectCountSubOne")
    R communityArticleCollectCountSubOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    // 社区文章点赞人数 + 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleLikeCountAddOne")
    R communityArticleLikeCountAddOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    // 社区文章点赞人数 - 1
    @PutMapping("/monkey-search/community/article/feign/communityArticleLikeCountSubOne")
    R communityArticleLikeCountSubOne(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId);

    // 更新社区文章评分
    @PutMapping("/monkey-search/community/article/feign/updateCourseScore")
    R updateCourseScore(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                        @RequestParam("score") @ApiParam("社区文章评分") Float score);

    // 社区成员数 + 1
    @PutMapping("/monkey-search/community/feign/communityMemberAddOne")
    R communityMemberAddOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId);

    // 社区成员数 - 1
    @PutMapping("/monkey-search/community/feign/communityMemberSubOne")
    R communityMemberSubOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId);

    // 社区文章数 + 1
    @PutMapping("/monkey-search/community/feign/communityArticleAddOne")
    R communityArticleAddOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId);

    // 社区文章数 - 1
    @PutMapping("/monkey-search/community/feign/communityArticleSubOne")
    R communityArticleSubOne(@RequestParam("communityId") @ApiParam("社区id") Long communityId);

    // 创建社区
    @PutMapping("/monkey-search/community/feign/createCommunity")
    R createCommunity(@RequestParam("communityStr") @ApiParam("社区索引类") String communityStr);

    // 删除社区
    @DeleteMapping("/monkey-search/community/feign/deleteCommunity")
    R deleteCommunity(@RequestParam("communityId") @ApiParam("社区id") Long communityId);

    // 用户游览数 + 1
    @PutMapping("/monkey-search/user/feign/userViewAddOne")
    R userViewAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户作品数 + 1
    @PutMapping("/monkey-search/user/feign/userOpusCountAddOne")
    R userOpusCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);


    // 用户作品数 - 1
    @PutMapping("/monkey-search/user/feign/userOpusCountSubOne")
    R userOpusCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 + 1
    @PutMapping("/monkey-search/user/feign/userLikeCountAddOne")
    R userLikeCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 - 1
    @PutMapping("/monkey-search/user/feign/userLikeCountSubOne")
    R userLikeCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 + 1
    @PutMapping("/monkey-search/user/feign/userCollectCountAddOne")
    R userCollectCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 - 1
    @PutMapping("/monkey-search/user/feign/userCollectCountSubOne")
    R userCollectCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);
}

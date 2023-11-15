package com.monkey.monkeyresource.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "resource-to-search")
public interface ResourceToSearchFeign {
    // 资源游览数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceViewAddOne")
    R resourceViewAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源评论数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceCommentCountAdd")
    R resourceCommentCountAdd(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源评论数减去对应值
    @PutMapping("/monkey-search/resource/feign/resourceCommentSub")
    R resourceCommentSub(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                       @RequestParam("sum") @ApiParam("减去的数目") Long sum);

    // 资源收藏数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceCollectCountAddOne")
    R resourceCollectCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源收藏数 - 1
    @PutMapping("/monkey-search/resource/feign/resourceCollectCountSubOne")
    R resourceCollectCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源下载人数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceDownCountAddOne")
    R resourceDownCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源购买人数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceBuyCountAddOne")
    R resourceBuyCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源购买人数 - 1
    @PutMapping("/monkey-search/resource/feign/resourceBuyCountSubOne")
    R resourceBuyCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 更新资源评分
    @PutMapping("/monkey-search/resource/feign/updateResourceScore")
    R updateResourceScore(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                        @RequestParam("score") @ApiParam("资源评分") Float score);

    // 插入资源索引
    @PostMapping("/monkey-search/resource/feign/insertResourceIndex")
    R insertResourceIndex(@RequestParam("resourceStr") @ApiParam("资源字符串") String resourceStr);

    // 删除资源索引
    @DeleteMapping("/monkey-search/resource/feign/deleteResourceIndex")
    R deleteResourceIndex(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源点赞数 + 1
    @PutMapping("/monkey-search/resource/feign/resourceLikeCountAddOne")
    R resourceLikeCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

    // 资源点赞数 - 1
    @PutMapping("/monkey-search/resource/feign/resourceLikeCountSubOne")
    R resourceLikeCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId);

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

package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.crypto.Data;
import java.util.Date;

@FeignClient(value = "monkey-community", contextId = "user-to-community")
public interface UserToCommunityFeignService {

    // 得到社区文章近一周发表数
    @GetMapping("/monkey-community/user/feign/queryCommunityArticleCountRecentlyWeek")
    R queryCommunityArticleCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId);

    @PostMapping("/monkey-community/user/feign/community/article/collect/add/one/{communityArticleId}")
    void communityArticleCollectAddOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId);

    @PostMapping("/monkey-community/user/feign/community/article/collect/sub/one/{communityArticleId}")
    void communityArticleCollectSubOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId,
                                       @RequestParam("createTime") @ApiParam("创建时间")Date createTime);

    @GetMapping("/monkey-community/user/feign/queryCommunityArticleById/{communityArticleId}")
    R queryCommunityArticleById(@PathVariable @ApiParam("社区文章id") Long communityArticleId);

    @GetMapping("/monkey-community/user/feign/queryCommunityArticleAndCommentById")
    R queryCommunityArticleAndCommentById(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                                 @RequestParam("commentId") @ApiParam("评论id") Long commentId);

    @GetMapping("/monkey-community/user/feign/queryCommunityArticleAuthorById")
    Long queryCommunityArticleAuthorById(@RequestParam("associationId") @ApiParam("文章id") Long communityArticleId);
}

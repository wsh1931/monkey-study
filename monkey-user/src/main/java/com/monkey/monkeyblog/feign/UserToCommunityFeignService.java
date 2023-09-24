package com.monkey.monkeyblog.feign;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "monkey-community", contextId = "user-to-community")
public interface UserToCommunityFeignService {

    @PostMapping("/monkey-community/user/feign/community/article/collect/add/one/{communityArticleId}")
    void communityArticleCollectAddOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId);

    @PostMapping("/monkey-community/user/feign/community/article/collect/sub/one/{communityArticleId}")
    void communityArticleCollectSubOne(@PathVariable @ApiParam("社区文章id") Long communityArticleId);
}

package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "monkey-community", contextId = "search-to-community")
public interface SearchToCommunityFeign {
    // 查询所有社区文章
    @GetMapping("/monkey-community/search/feign/queryAllCommunityArticle")
    R queryAllCommunityArticle();

    // 查询所有社区
    @GetMapping("/monkey-community/search/feign/queryAllCommunity")
    R queryAllCommunity();

    // 得到所有用户所有社区文章，点赞，收藏，游览数
    @GetMapping("/monkey-community/search/feign/queryAllUserCommunityArticleInfo")
    R queryAllUserCommunityArticleInfo();
}
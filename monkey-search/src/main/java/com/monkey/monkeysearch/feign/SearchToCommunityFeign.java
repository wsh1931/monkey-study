package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "monkey-community", contextId = "search-to-community")
public interface SearchToCommunityFeign {
    @GetMapping("/monkey-community/search/feign/queryAllCommunityArticle")
    R queryAllCommunityArticle();
}

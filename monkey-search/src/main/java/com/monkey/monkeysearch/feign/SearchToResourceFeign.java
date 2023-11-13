package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "monkey-resource", contextId = "search-to-resource")
public interface SearchToResourceFeign {
    // 查询所有资源
    @GetMapping("/monkey-resource/search/feign/queryAllResource")
    R queryAllResource();
}

package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: wusihao
 * @date: 2023/11/8 8:52
 * @version: 1.0
 * @description:
 */
@FeignClient(value = "monkey-article", contextId = "search-to-article")
public interface SearchToArticleFeign {

    @GetMapping("/monkey-article/search/feign/queryAllArticle")
    R queryAllArticle();

    // 得到所有用户所有文章，点赞，收藏，游览数
    @GetMapping("/monkey-article/search/feign/queryAllUserArticleInfo")
    R queryAllUserArticleInfo();
}

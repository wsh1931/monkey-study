package com.monkey.monkeyarticle.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "article-to-search")
public interface ArticleToSearchFeignService {
    // 文章游览数 + 1
    @PutMapping("/monkey-search/article/feign/articleViewAddOne")
    R articleViewAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId);

    // 文章评论数 + 1
    @PutMapping("/monkey-search/article/feign/articleCommentCountAdd")
    R articleCommentCountAdd(@RequestParam("articleId") @ApiParam("文章id") Long articleId);

    // 文章点赞数 + 1
    @PutMapping("/monkey-search/article/feign/articleLikeCountAddOne")
    R articleLikeCountAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId);

    // 文章点赞数 - 1
    @PutMapping("/monkey-search/article/feign/articleLikeCountSubOne")
    R articleLikeCountSubOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId);

    // 文章收藏数 + 1
    @PutMapping("/monkey-search/article/feign/articleCollectCountAddOne")
    R articleCollectCountAddOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId);

    // 文章收藏数 - 1
    @PutMapping("/monkey-search/article/feign/articleCollectCountSubOne")
    R articleCollectCountSubOne(@RequestParam("articleId") @ApiParam("文章id") Long articleId);
}

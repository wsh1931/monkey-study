package com.monkey.monkeyarticle.feign;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.pojo.Article;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "monkey-search", contextId = "article-to-search")
public interface ArticleToSearchFeignService {

    // 删除文章
    @DeleteMapping("/monkey-search/article/feign/deleteArticle")
    R deleteArticle(@RequestParam("articleId") @ApiParam("文章id") String articleId);
    // 更新文章信息
    @PutMapping("/monkey-search/article/feign/updateArticle")
    R updateArticle(@RequestParam @ApiParam("文章实体类") String article);

    // 通过用户id得到用户vo信息
    @GetMapping("/monkey-search/article/feign/getUserInformationByUserId")
    ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data);
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

    // 得到作者信息
    @GetMapping("/monkey-search/user/feign/getAuthorInfoById")
    R getAuthorInfoById(@RequestParam("authorId") @ApiParam("作者id") Long authorId);
}

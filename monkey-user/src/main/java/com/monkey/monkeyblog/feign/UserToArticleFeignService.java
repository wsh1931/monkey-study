package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: wusihao
 * @date: 2023/7/31 8:50
 * @version: 1.0
 * @description:
 */
@FeignClient(value = "monkey-article", contextId = "user-to-article")
public interface UserToArticleFeignService {

    // 通过用户id得到用户发表文章信息
    @GetMapping("/monkey-article/user/feign/getUserArticleCountByUserId/{userId}")
    R getUserArticleCountByUserId(@PathVariable Long userId);

    // 通过文章id得到文章点赞数
    @GetMapping("/monkey-article/user/feign/getArticleLikeCountByArticleId/{articleId}")
    R getArticleLikeCountByArticleId(@PathVariable Long articleId);

    // 通过文章id得到文章评论数
    @GetMapping("/monkey-article/user/feign/getArticleCommentCountByArticleId/{articleId}")
    R getArticleCommentCountByArticleId(@PathVariable Long articleId);

    // 通过文章id得到文章标签列表
    @GetMapping("/monkey-article/user/feign/getArticleLabelListByLabelId/{labelId}")
    R getArticleLabelListByLabelId(@PathVariable Long labelId);

    // 通过文章id得到文章标签列表
    @GetMapping("/monkey-article/user/feign/getArticleLabelListByarticleId/{articleId}")
    R getArticleLabelListByarticleId(@PathVariable Long articleId);

    // 通过用户id得到文章分页列表
    @GetMapping("/monkey-article/user/feign/getArticleListByUserId")
    R getArticleListByUserId(@RequestParam("currentPage") Long currentPage,
                                    @RequestParam("pageSize") Long pageSize,
                                    @RequestParam("labelId") Long labelId,
                                    @RequestParam("userId") String userId);
}

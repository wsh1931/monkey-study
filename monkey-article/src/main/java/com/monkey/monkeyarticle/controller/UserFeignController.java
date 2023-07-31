package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.service.UserFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wusihao
 * @date: 2023/7/31 9:35
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-article/user/feign")
public class UserFeignController {
    @Autowired
    private UserFeignService userFeignService;

    // 通过用户id得到用户发表文章信息
    @GetMapping("/getUserArticleCountByUserId/{userId}")
    public R getUserArticleCountByUserId(@PathVariable Long userId) {
        return userFeignService.getUserArticleCountByUserId(userId);
    }

    // 通过文章id得到文章点赞数
    @GetMapping("/getArticleLikeCountByArticleId/{articleId}")
    public R getArticleLikeCountByArticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleLikeCountByArticleId(articleId);
    }

    // 通过文章id得到文章评论数
    @GetMapping("getArticleCommentCountByArticleId/{articleId}")
    public R getArticleCommentCountByArticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleCommentCountByArticleId(articleId);
    }

    // 通过标签id得到文章标签列表
    @GetMapping("/getArticleLabelListByLabelId/{labelId}")
    public R getArticleLabelListByLabelId(@PathVariable Long labelId) {
        return userFeignService.getArticleLabelListByLabelId(labelId);
    }

    // 通过文章id得到文章标签列表
    @GetMapping("/getArticleLabelListByarticleId/{articleId}")
    public R getArticleLabelListByarticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleLabelListByarticleId(articleId);
    }

    // 通过用户id得到文章分页列表
    @GetMapping("/getArticleListByUserId")
    public R getArticleListByUserId(@RequestParam("currentPage") Long currentPage,
                                    @RequestParam("pageSize") Long pageSize,
                                    @RequestParam("labelId") Long labelId,
                                    @RequestParam("userId") String userId) {
        return userFeignService.getArticleListByUserId(currentPage, pageSize, labelId, userId);
    }
}

package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/7/31 9:35
 * @version: 1.0
 * @description:
 */
@Api(tags = "文章调用用户模块接口")
@RestController
@RequestMapping("/monkey-article/user/feign")
public class UserFeignController {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("通过用户id得到用户发表文章信息")
    @GetMapping("/getUserArticleCountByUserId/{userId}")
    public R getUserArticleCountByUserId(@PathVariable Long userId) {
        return userFeignService.getUserArticleCountByUserId(userId);
    }

    @ApiOperation("通过文章id得到文章点赞数")
    @GetMapping("/getArticleLikeCountByArticleId/{articleId}")
    public R getArticleLikeCountByArticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleLikeCountByArticleId(articleId);
    }

    @ApiOperation("通过文章id得到文章评论数")
    @GetMapping("getArticleCommentCountByArticleId/{articleId}")
    public R getArticleCommentCountByArticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleCommentCountByArticleId(articleId);
    }

    @ApiOperation("通过标签id得到文章标签列表")
    @GetMapping("/getArticleLabelListByLabelId/{labelId}")
    public R getArticleLabelListByLabelId(@PathVariable Long labelId) {
        return userFeignService.getArticleLabelListByLabelId(labelId);
    }

    @ApiOperation("通过文章id得到文章标签列表")
    @GetMapping("/getArticleLabelListByarticleId/{articleId}")
    public R getArticleLabelListByarticleId(@PathVariable Long articleId) {
        return userFeignService.getArticleLabelListByarticleId(articleId);
    }

    @ApiOperation("通过用户id得到文章分页列表")
    @GetMapping("/getArticleListByUserId")
    public R getArticleListByUserId(@RequestParam("currentPage") Long currentPage,
                                    @RequestParam("pageSize") Long pageSize,
                                    @RequestParam("labelId") Long labelId,
                                    @RequestParam("userId") String userId) {
        return userFeignService.getArticleListByUserId(currentPage, pageSize, labelId, userId);
    }

    @ApiOperation("更新文章信息, 文章收藏数 + 1")
    @PutMapping("/addUpdateArticleInfo/{articleId}")
    public R addUpdateArticleInfo(@PathVariable Long articleId) {
        return userFeignService.updateArticleInfo(articleId);
    }

    @ApiOperation("更新文章信息, 文章收藏数 - 1")
    @PutMapping("/subUpdateArticleInfo/{articleId}")
    public R subUpdateArticleInfo(@PathVariable Long articleId) {
        return userFeignService.subUpdateArticleInfo(articleId);
    }
}

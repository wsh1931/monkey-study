package com.monkey.monkeyarticle.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.service.ArticleEditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/30 8:46
 * @version: 1.0
 * @description:
 */
@Api(tags = "通过文章id查询文章信息")
@RestController
@RequestMapping("/monkey-article/edit")
public class ArticleEditController {
    @Resource
    private ArticleEditService articleEditService;

    @ApiOperation("通过文章id查询文章信息")
    @GetMapping("/queryArticleInfoById")
    public R queryArticleInfoById(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleEditService.queryArticleInfoById(articleId);
    }

    @ApiOperation("删除图片")
    @DeleteMapping("/deleteArticlePicture")
    @PreAuthorize("@articleCuscomAuthority.judgeIsAuthor(#articleId)")
    public R deleteArticlePicture(@RequestParam("articleId") @ApiParam("文章id") Long articleId) {
        return articleEditService.deleteArticlePicture(articleId);
    }

    @ApiOperation("更新文章")
    @PutMapping("/updateArticle")
    @PreAuthorize("@articleCuscomAuthority.judgeIsAuthor(#articleId)")
    public R updateArticle(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                           @RequestParam("articleStr") @ApiParam("文章实体类字符串") String articleStr) {
        Article article = JSONObject.parseObject(articleStr, Article.class);
        return articleEditService.updateArticle(article);
    }

    @ApiOperation("更新文章图片")
    @PutMapping("/uploadArticlePicture")
    @PreAuthorize("@articleCuscomAuthority.judgeIsAuthor(#articleId)")
    public R uploadArticlePicture(@RequestParam("articleId") @ApiParam("文章id") Long articleId,
                                  @RequestParam("photo") @ApiParam("上传图片地址") String photo) {
        return articleEditService.uploadArticlePicture(photo, articleId);
    }
}

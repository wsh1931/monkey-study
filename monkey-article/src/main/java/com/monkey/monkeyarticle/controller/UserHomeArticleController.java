package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.service.UserHomeArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/29 16:33
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户主页调用文章模块接口")
@RestController
@RequestMapping("/monkey-article/user/home")
public class UserHomeArticleController {
    @Resource
    private UserHomeArticleService userHomeArticleService;

    @ApiOperation("通过用户id查询文章集合")
    @GetMapping("/queryArticleByUserId")
    public R queryArticleByUserId(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                  @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                  @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        return userHomeArticleService.queryArticleByUserId(userId, currentPage, pageSize);
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/deleteArticle")
    @PreAuthorize("@articleCuscomAuthority.judgeIsAuthor(#articleId)")
    public R deleteArticle(@RequestParam("articleId") @ApiParam("文章id") String articleId) {
        return userHomeArticleService.deleteArticle(articleId);
    }
}

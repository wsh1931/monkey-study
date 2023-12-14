package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.service.BlogArticleService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Api(tags = "文章主页接口")
@RequestMapping("/monkey-article/blog")
public class BlogArticleController {

    @Resource
    private BlogArticleService blogArticleService;

    @ApiOperation("通过标签id得到文章内容")
    @GetMapping("/getArticleContentByLabelId")
    private ResultVO getArticleContentByLabelId(@RequestParam("labelId") @ApiParam("标签id") String labelId) {
        return blogArticleService.getArticleContentByLabelId(labelId);
    }

    @ApiOperation("博客主页得到所有文章以及分页功能实现")
    @GetMapping("/getArticlePagination")
    private ResultVO getArticlePagination(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                          @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize,
                                          @RequestParam("labelId") @ApiParam("标签id")Long labelId) {
        String userId = JwtUtil.getUserId();
        return blogArticleService.getArticlePagination(currentPage, pageSize, labelId, userId);
    }

    @ApiOperation("查询最近热帖")
    @GetMapping("/fireRecently")
    private ResultVO getRecentlyFireArticle() {
        return blogArticleService.getRecentlyFireArticle();
    }

    @ApiOperation("用户点赞文章")
    @GetMapping("/userClickPraise")
    private ResultVO userClickPraise(@RequestParam("articleId") @ApiParam("文章id")Long articleId,
                                     @RequestParam("recipientId") @ApiParam("消息接收者id")Long recipientId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return blogArticleService.userClickPraise(articleId, userId, recipientId);
    }

    @ApiOperation("用户取消点赞")
    @GetMapping("/userClickOppose")
    private ResultVO userClickOppose(@RequestParam("articleId") @ApiParam("文章id")Long articleId,
                                     @RequestParam("authorId") @ApiParam("作者id") Long authorId,
                                     @RequestParam("createTime") String createTime) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return blogArticleService.userClickOppose(articleId, userId, authorId, createTime);
    }

    @ApiOperation("通过文章id得到文章信息")
    @GetMapping("/getArticleInformationByArticleId")
    private ResultVO getArticleInformationByArticleId(@RequestParam("articleId") @ApiParam("文章id")Long articleId) {
        String userId = JwtUtil.getUserId();
        return blogArticleService.getArticleInformationByArticleId(articleId, userId);
    }

    @ApiOperation("按排序字段得到文章列表")
    @GetMapping("/getArticleListBySort")
    public ResultVO getArticleListBySort() {
        return blogArticleService.getArticleListBySort();
    }
}

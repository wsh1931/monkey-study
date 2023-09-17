package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.PublishService;
import io.netty.handler.ssl.util.LazyX509Certificate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "发布文章接口")
@RestController
@RequestMapping("/monkey-article/publish")
public class PublishController {
    @Resource
    private PublishService publishService;

    @PostMapping("/article")
    public ResultVO publishArticle(@RequestParam("content") @ApiParam("发布文章内容")String content,
                                   @RequestParam("profile") @ApiParam("发布文章简介")String profile,
                                   @RequestParam("photo") @ApiParam("发布文章图片地址 ")String photo,
                                   @RequestParam("title") @ApiParam("发布文章标题")String title,
                                   @RequestParam("labelId") @ApiParam("文章标签id")String labelId) {
        return publishService.publishArticle(content, profile, photo, title, labelId);
    }

    // 得到一级标签列表
    @GetMapping("/getOneLevelLabelList")
    public ResultVO getOneLevelLabelList() {
        return publishService.getOneLevelLabelList();
    }

    // 通过一级标签id查询二级标签列表
    @GetMapping("/getTwoLabelListByOneLabelId")
    public ResultVO getTwoLabelListByOneLabelId(@RequestParam("labelOneId") @ApiParam("一级标签id")Long labelOneId) {
        return publishService.getTwoLabelListByOneLabelId(labelOneId);
    }

    // 通过标签名模糊查找一级标签
    @GetMapping("/likeSearchOneLabel")
    public R likeSearchOneLabel(@RequestParam("name") @ApiParam("模糊查找文章字段")String name) {
        return publishService.likeSearchOneLabel(name);
    }
}

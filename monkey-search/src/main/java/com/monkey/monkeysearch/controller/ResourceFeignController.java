package com.monkey.monkeysearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESResourceIndex;
import com.monkey.monkeysearch.service.ResourceFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/13 8:13
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源模块调用搜素模块feign接口")
@RestController
@RequestMapping("/monkey-search/resource/feign")
public class ResourceFeignController {
    @Resource
    private ResourceFeignService resourceFeignService;
    @ApiOperation("资源游览数 + 1")
    @PutMapping("/resourceViewAddOne")
    public R resourceViewAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceViewAddOne(resourceId);
    }

    @ApiOperation("资源评论数 + 1")
    @PutMapping("/resourceCommentCountAdd")
    public R resourceCommentCountAdd(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceCommentCountAdd(resourceId);
    }

    @ApiOperation("资源评论数减去对应值")
    @PutMapping("/resourceCommentSub")
    public R resourceCommentSub(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                              @RequestParam("sum") @ApiParam("减去的数目") Long sum) {
        return resourceFeignService.resourceCommentSub(resourceId, sum);
    }

    @ApiOperation("资源收藏数 + 1")
    @PutMapping("/resourceCollectCountAddOne")
    public R resourceCollectCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceCollectCountAddOne(resourceId);
    }
    @ApiOperation("资源收藏数 - 1")
    @PutMapping("/resourceCollectCountSubOne")
    public R resourceCollectCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceCollectCountSubOne(resourceId);
    }

    @ApiOperation("资源下载人数 + 1")
    @PutMapping("/resourceDownCountAddOne")
    public R resourceDownCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceDownCountAddOne(resourceId);
    }

    @ApiOperation("资源购买人数 + 1")
    @PutMapping("/resourceBuyCountAddOne")
    public R resourceBuyCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceBuyCountAddOne(resourceId);
    }

    @ApiOperation("资源购买人数 - 1")
    @PutMapping("/resourceBuyCountSubOne")
    public R resourceBuyCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceBuyCountSubOne(resourceId);
    }

    @ApiOperation("资源点赞人数 - 1")
    @PutMapping("/resourceLikeCountSubOne")
    public R resourceLikeCountSubOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceLikeCountSubOne(resourceId);
    }

    @ApiOperation("资源点赞人数 + 1")
    @PutMapping("/resourceLikeCountAddOne")
    public R resourceLikeCountAddOne(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.resourceLikeCountAddOne(resourceId);
    }


    @ApiOperation("更新资源评分")
    @PutMapping("/updateResourceScore")
    public R updateResourceScore(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                               @RequestParam("score") @ApiParam("资源评分") Float score) {
        return resourceFeignService.updateResourceScore(resourceId, score);
    }

    @ApiOperation("插入资源索引")
    @PostMapping("/insertResourceIndex")
    public R insertResourceIndex(@RequestParam("resourceStr") @ApiParam("资源字符串") String resourceStr) {
        ESResourceIndex esResourceIndex = JSONObject.parseObject(resourceStr, ESResourceIndex.class);
        return resourceFeignService.insertResourceIndex(esResourceIndex);
    }

    @ApiOperation("删除资源索引")
    @DeleteMapping("/deleteResourceIndex")
    public R deleteResourceIndex(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        return resourceFeignService.deleteResourceIndex(resourceId);
    }
}

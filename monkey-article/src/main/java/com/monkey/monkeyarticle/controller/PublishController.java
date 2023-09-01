package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.PublishService;
import io.netty.handler.ssl.util.LazyX509Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/monkey-article/publish")
public class PublishController {
    @Autowired
    private PublishService publishService;

    @PostMapping("/article")
    public ResultVO publishArticle(@RequestParam Map<String, String> data) {
        String content = data.get("content");
        String profile = data.get("profile");
        String photo = data.get("photo");
        String title = data.get("title");
        String labelId = data.get("labelId");
        return publishService.publishArticle(content, profile, photo, title, labelId);
    }

    // 得到一级标签列表
    @GetMapping("/getOneLevelLabelList")
    public ResultVO getOneLevelLabelList() {
        return publishService.getOneLevelLabelList();
    }

    // 通过一级标签id查询二级标签列表
    @GetMapping("/getTwoLabelListByOneLabelId")
    public ResultVO getTwoLabelListByOneLabelId(@RequestParam Map<String, String> data) {
        Long labelOneId = Long.parseLong(data.get("labelOneId"));
        return publishService.getTwoLabelListByOneLabelId(labelOneId);
    }

    // 通过标签名模糊查找一级标签
    @GetMapping("/likeSearchOneLabel")
    public R likeSearchOneLabel(@RequestParam Map<String, String> data) {
        String name = data.get("name");
        return publishService.likeSearchOneLabel(name);
    }
}

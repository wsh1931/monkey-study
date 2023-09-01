package com.monkey.monkeycommunity.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.service.CommunityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/1 17:35
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-community/community")
public class CommunityController {
    @Resource
    private CommunityService communityService;

    // 得到一级标签
    @GetMapping("/getOneLevelLabelList")
    public R getOneLevelLabelList() {
        return communityService.getOneLevelLabelList();
    }

    // 通过一级标签id得到二级标签列表
    @GetMapping("/getTwoLabelListByOneLabelId")
    public R getTwoLabelListByOneLabelId(@RequestParam Map<String, String> data) {
        long labelOneId = Long.parseLong(data.get("labelOneId"));
        return communityService.getTwoLabelListByOneLabelId(labelOneId);
    }
}

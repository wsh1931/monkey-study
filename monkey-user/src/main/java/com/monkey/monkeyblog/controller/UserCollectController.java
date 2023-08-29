package com.monkey.monkeyblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/31 17:07
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-user/collect")
public class UserCollectController {
    @Autowired
    private UserCollectService userCollectService;

    // 通过用户id得到用户收藏目录
    @GetMapping("/getCollectContentListByUserId")
    public R getCollectContentListByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long associateId = Long.parseLong(data.get("associateId"));
        int collectType = Integer.parseInt(data.get("collectType"));
        return userCollectService.getCollectContentListByUserId(userId, associateId, collectType);
    }

    // 创建收藏夹
    @PostMapping("/create/content")
    public R createContent(@RequestParam Map<String, String> data) {
        CollectContent content = JSONObject.parseObject(data.get("content"), CollectContent.class);
        return userCollectService.createContent(content);
    }

    // 收藏功能实现
    @PostMapping("/collectContent")
    public R collectContent(@RequestParam Map<String, String> data) {
        long collectContentId = Long.parseLong(data.get("collectContentId"));
        long associateId = Long.parseLong(data.get("associateId"));
        int collectType = Integer.parseInt(data.get("collectType"));
        String collectTitle = data.get("collectTitle");
        long userId = Long.parseLong(data.get("userId"));
        return userCollectService.collectContent(collectContentId, associateId, collectType, collectTitle, userId);
    }
}

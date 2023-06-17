package com.monkey.monkeyarticle.service.publish;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface PublishService {
    // 发布文章
    ResultVO publishArticle(Map<String, String> data);
}

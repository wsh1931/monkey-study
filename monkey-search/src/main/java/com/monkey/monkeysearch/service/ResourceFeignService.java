package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESResourceIndex;

public interface ResourceFeignService {
    // 资源游览数 + 1
    R resourceViewAddOne(Long resourceId);

    // 资源评论数 + 1
    R resourceCommentCountAdd(Long resourceId);


    // 资源收藏数 + 1
    R resourceCollectCountAddOne(Long resourceId);

    // 资源收藏数 - 1
    R resourceCollectCountSubOne(Long resourceId);

    // 资源购买人数 + 1
    R resourceBuyCountAddOne(Long resourceId);

    // 资源购买人数 - 1
    R resourceBuyCountSubOne(Long resourceId);

    // 资源下载人数 + 1
    R resourceDownCountAddOne(Long resourceId);

    // 更新资源评分
    R updateResourceScore(Long resourceId, Float score);

    // 资源评论数减去对应值
    R resourceCommentSub(Long resourceId, Long sum);

    // 插入资源索引
    R insertResourceIndex(ESResourceIndex esResourceIndex);

    // 删除资源索引
    R deleteResourceIndex(Long resourceId);

    // 资源点赞人数 - 1
    R resourceLikeCountSubOne(Long resourceId);

    // 资源点赞人数 + 1
    R resourceLikeCountAddOne(Long resourceId);
}

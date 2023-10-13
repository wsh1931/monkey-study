package com.monkey.monkeyresource.redis;

/**
 * @author: wusihao
 * @date: 2023/10/12 14:54
 * @version: 1.0
 * @description:
 */
public class RedisKeyConstant {
    // oneClassification(每天凌晨4点更新, 防止数据丢失)
    public static final String oneClassification = "oneClassification";
    // 形式为oneClassificationList + 一级标签id
    public static final String twoClassificationList = "twoClassificationList";

    public static final String createResourceUserRank = "createResourceUserRank";
}
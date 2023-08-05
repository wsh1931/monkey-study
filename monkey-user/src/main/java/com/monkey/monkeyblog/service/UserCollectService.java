package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.result.R;

public interface UserCollectService {
    // 通过用户id得到用户收藏目录
    R getCollectContentListByUserId(long userId, long associateId, long collectType);

    // 创建收藏夹
    R createContent(CollectContent content);

    // // 收藏功能实现
    R collectContent(long collectContentId, long associateId, int collectType, String collectTitle, long userId);
}

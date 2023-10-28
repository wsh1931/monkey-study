package com.monkey.monkeyblog.service.message;

import com.monkey.monkeyUtils.result.R;

public interface MessageCenterService {
    // 查询未查看评论回复消息数
    R queryNoCheckCommentCount(String userId);

    // 查询未查看点赞数
    R queryNoCheckLikeCount(String userId);
}

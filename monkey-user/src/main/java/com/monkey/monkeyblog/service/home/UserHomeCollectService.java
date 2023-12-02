package com.monkey.monkeyblog.service.home;

import com.monkey.monkeyUtils.result.R;

public interface UserHomeCollectService {
    R queryUserCollectContent(String userId, Long currentPage, Integer pageSize);

    // 查询收藏详细内容
    R queryCollectContentDetail(String collectContentId);
}

package com.monkey.monkeyblog.service.home;

import com.monkey.monkeyUtils.result.R;
import io.swagger.models.auth.In;

public interface UserHomeConnectService {
    // 查询用户关注列表
    R queryUserConnectById(String userId, Long currentPage, Integer pageSize);
}

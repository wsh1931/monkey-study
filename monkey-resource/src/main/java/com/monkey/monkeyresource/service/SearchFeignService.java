package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface SearchFeignService {
    // 查询所有资源
    R queryAllResource();

    // 得到所有用户所有资源，点赞，收藏，游览数
    R queryAllUserResourceInfo();
}

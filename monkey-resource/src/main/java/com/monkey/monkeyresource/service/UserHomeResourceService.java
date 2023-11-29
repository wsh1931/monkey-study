package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;

public interface UserHomeResourceService {
    // 通过用户id查询资源集合
    R queryResourceByUserId(Long userId, Long currentPage, Integer pageSize);


    // 删除资源
    R deleteResource(Long resourceId);
}

package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;

public interface UserHomeResourceService {
    // 通过用户id查询资源集合
    R queryResourceByUserId(Long userId, Long currentPage, Integer pageSize);

    // 通过资源id得到资源信息
    R queryResourceById(Long resourceId);

    // 更新资源
    R updateResource(UploadResourcesVo uploadResourcesVo);

    // 删除资源
    R deleteResource(Long resourceId);
}

package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;

public interface ResourceEditService {

    // 通过资源id得到资源信息
    R queryResourceById(Long resourceId);

    // 更新资源
    R updateResource(UploadResourcesVo uploadResourcesVo);
}

package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResourceDetailService {
    // 查询资源标签列表
    R queryResourceInfo(Long resourceId);

    // 下载文件资源
    void downFileResource(HttpServletResponse response, HttpServletRequest request, Long resourceId);

    // 查询资源评价信息
    R queryResourceEvaluateInfo(Long resourceId);

    // 查询相关资源列表
    R resourceEvaluateInfo(Long resourceId);
}

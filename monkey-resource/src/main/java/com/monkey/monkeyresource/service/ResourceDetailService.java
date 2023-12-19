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

    // 判断用户是否点赞或收藏此资源
    R judgeUserIsLikeOrCollectResource(String userId, Long resourceId);

    // 点赞资源
    R likeResource(long userId, Long resourceId, Long recipientId);

    // 取消点赞资源
    R cancelLikeResource(long userId, Long resourceId, long authorId);
    // 精选资源
    R curationResource(long userId, Long resourceId);

    // 取消精选资源
    R cancelCurationResource(long userId, Long resourceId);

    // 判断资源是否存在
    R judgeResourceIsExist(Long resourceId);
}

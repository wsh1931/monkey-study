package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface UserFeignService {
    // 删除用户购买资源记录
    R deleteUserBuyResource(Long userId, Long resourceId);

    // 资源收藏数 + 1
    R resourceCollectCountAddOne(Long resourceId);

    // 资源收藏数 - 1
    R resourceCollectCountSubOne(Long resourceId);

    // 通过资源id得到资源信息
    R queryResourceById(Long resourceId);

    // 通过资源id,和评论id得到资源信息
    R queryResourceAndCommentById(Long resourceId, Long commentId);

    // 通过资源id得到资源作者id
    Long queryResourceAuthorById(Long resourceId);

}

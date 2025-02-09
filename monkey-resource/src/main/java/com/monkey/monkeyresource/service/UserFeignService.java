package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

import java.util.Date;

public interface UserFeignService {
    // 删除用户购买资源记录
    R deleteUserBuyResource(Long userId, Long resourceId, Float money, Date payTime);

    // 资源收藏数 + 1
    R resourceCollectCountAddOne(Long resourceId);

    // 资源收藏数 - 1
    R resourceCollectCountSubOne(Long resourceId, Date createTime);

    // 通过资源id得到资源信息
    R queryResourceById(Long resourceId);

    // 通过资源id,和评论id得到资源信息
    R queryResourceAndCommentById(Long resourceId, Long commentId);

    // 通过资源id得到资源作者id
    Long queryResourceAuthorById(Long resourceId);

    // 通过资源，评论id得到资源信息
    R queryResourceAndCommentInfoById(Long resourceId, Long commentId);

    // 得到资源一周发表数
    R queryResourceCountRecentlyWeek(String userId);
}

package com.monkey.monkeyblog.service.center;

import com.monkey.monkeyUtils.result.R;

public interface UserCenterCollectService {
    // 查询收藏目录以及对应的收藏数
    R queryCollectContent(Long currentPage, Integer pageSize);

    // 查询收藏目录信息通过收藏目录id
    R queryCollectContentById(Long collectContentId);

    // 查询收藏目录关系列表
    R queryContentConnect(Long collectContentId, Integer type, String keyword, Long currentPage, Integer pageSize);

    // 更新收藏描述
    R updateCollectDescription(Long collectContentId, String description);

    // 更新收藏目录标题
    R updateCollectName(Long collectContentId, String name);

    // 删除收藏夹
    R deleteCollectContent(Long collectContentId);

    // 设置收藏夹为私密收藏夹
    R setCollectPrivate(Long collectContentId);

    // 设置收藏夹为公开收藏夹
    R setCollectPublic(Long collectContentId);
}

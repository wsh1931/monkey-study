package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESUserService {
    // 将用户数据库中所有数据存入elasticsearch用户文档中
    R insertUserDocument();

    // 查询综合用户列表
    R queryComprehensiveUser(Integer currentPage, Integer pageSize, String keyword);


    // 查询收藏数最多用户列表
    R queryCollectUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询点赞数最多用户列表
    R queryLikeUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询游览数最多用户列表
    R queryViewUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热用户列表
    R queryHireUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新用户列表
    R queryLatestUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询作品数最多用户列表
    R queryOpusUser(Integer currentPage, Integer pageSize, String keyword);

    // 查询粉丝数最多用户列表
    R queryFansUser(Integer currentPage, Integer pageSize, String keyword);

}

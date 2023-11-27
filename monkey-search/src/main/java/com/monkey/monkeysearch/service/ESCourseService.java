package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface ESCourseService {
    // 将课程数据库中所有数据存入elasticsearch课程文档中
    R insertCourseDocument();

    // 查询综合课程列表
    R queryComprehensiveCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询评论最多课程列表
    R queryCommentCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询收藏数最多课程列表
    R queryCollectCourse(Integer currentPage, Integer pageSize, String keyword);


    // 查询游览数最多课程列表
    R queryViewCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询最热课程列表
    R queryHireCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询最新课程列表
    R queryLatestCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询课程评分最高的课程列表
    R queryScoreCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询学习人数最多课程列表
    R queryStudyCourse(Integer currentPage, Integer pageSize, String keyword);

    // 查询所有课程文档
    R queryAllCourseDocument();

    // 删除所有课程文档
    R deleteAllCourseDocument();
}

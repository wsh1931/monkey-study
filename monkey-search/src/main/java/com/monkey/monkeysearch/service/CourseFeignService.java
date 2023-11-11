package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;

public interface CourseFeignService {
    // 课程游览数 + 1
    R courseViewAddOne(Long courseId);

    // 课程评论数 + 1
    R courseCommentCountAdd(Long courseId);


    // 课程收藏数 + 1
    R courseCollectCountAddOne(Long courseId);

    // 课程收藏数 - 1
    R courseCollectCountSubOne(Long courseId);

    // 课程学习人数 + 1
    R courseStudyCountAddOne(Long courseId);

    // 课程学习人数 - 1
    R courseStudyCountSubOne(Long courseId);

    // 更新课程评分
    R updateCourseScore(Long courseId, Float score);

    // 课程评论数减去对应值
    R courseCommentSub(Long courseId, Long sum);
}

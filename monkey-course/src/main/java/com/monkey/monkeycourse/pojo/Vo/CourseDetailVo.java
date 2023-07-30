package com.monkey.monkeycourse.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wusihao
 * @date: 2023/7/30 20:22
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailVo {
    // 课程id
    private Long id;
    // 课程标题
    private String title;
    // 课程收藏数
    private Long collectCount;
    // 学习人数
    private Long studentCount;
    // 课程评分
    private Float courseScore;
    // 是否免费
    private Integer isFree;
    // 课程原价
    private Float originalPrice;
    // 打折数
    private Float discount;
    // 打折之后的价格
    private Float discountPrice;
    // 是否收藏过该课程
    private Integer isCollect;
    // 你将收获
    private String harvest;
    // 适用人群
    private String suitPeople;
    // 课程介绍
    private String introduce;

    // 课程小节总数
    private Long subsectionCount;
    // 课程目录标题名称
    private String titleDirectory;
    // 课程目录是否免费
    private Integer directoryIsFree;
    // 课程目录播放时长
    private Long videoTime;
}

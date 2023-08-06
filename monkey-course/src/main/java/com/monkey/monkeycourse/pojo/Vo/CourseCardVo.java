package com.monkey.monkeycourse.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/7/27 15:11
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCardVo {
    // 主键id
    private Long id;
    // 课程封面
    private String picture;
    // 课程标题
    private String title;
    // 讲师简介
    private String teacherProfile;
    // 课程价格
    private String price;
    // 课程小节总数
    private Long sum;
    // 学习人数
    private Long studySum;
    // 课程形式类型
    private String courseFormType;
    // 0表示免费，1表示收费
    private Integer isFree;
    // 发布课程时间
    private Date createTime;

    // 课程小节数
    private Long sectionCount;

    // 教师名称
    private String teacherName;
}

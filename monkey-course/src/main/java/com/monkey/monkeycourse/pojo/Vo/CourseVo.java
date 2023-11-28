package com.monkey.monkeycourse.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeycourse.constant.CourseEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/28 10:42
 * @version: 1.0
 * @description:
 */
@Data
public class CourseVo {
    private Long id;
    private Long userId;
    private String title;
    private String introduce;
    private String picture;
    private Long formTypeId;
    private Float score;
    private Long collectCount;
    private Long sectionCount;
    private Long studyCount;
    private Long viewCount;
    private Long barrageCount;
    private Long commentCount;
    private Long evaluateCount;
    private Date createTime;
    private String formTypeName;
    private Integer isHover = CourseEnum.NOT_HOVER.getCode();
    private Integer isMoreHover = CourseEnum.NOT_HOVER.getCode();
}

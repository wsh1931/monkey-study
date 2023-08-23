package com.monkey.monkeycourse.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Long id;
    private Long userId;
    private String title;
    private String introduce;
    private String suitPeople;
    private String harvest;
    private String picture;
    private Float coursePrice;
    private Integer isDiscount;
    private Float discount;
    private Long formTypeId;
    private Float score;
    private Long collectCount;
    private Long sectionCount;
    private Long studyCount;
    private Integer isFree;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    private String discountPrice;
}

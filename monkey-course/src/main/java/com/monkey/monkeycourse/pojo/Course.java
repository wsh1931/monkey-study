package com.monkey.monkeycourse.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @description:
 * @date: 2023/7/12 17:28
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @TableId(type = IdType.AUTO)
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
    private Long viewCount;
    private Long barrageCount;
    private Long commentCount;
    private Long evaluateCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private String discountPrice;

    // 是否已经认证QQ邮箱
    @TableField(exist = false)
    public Integer hasEmail;

    // 用户邮箱
    @TableField(exist = false)
    public String email;

    @TableField(exist = false)
    private List<String> labelName;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String userHeadImg;
    @TableField(exist = false)
    private String userBrief;

    @TableField(exist = false)
    private String formTypeName;
}

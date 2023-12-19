package com.monkey.monkeycourse.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/15 11:01
 * @version: 1.0
 * @description:
 */
@Data
public class CourseStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer studyCount;
    private Integer buyCount;
    private BigDecimal harvestMoney;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/15 11:08
 * @version: 1.0
 * @description:
 */
@Data
public class ResourceStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long resourceId;
    private Integer viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer buyCount;
    private Integer likeCount;
    private Integer downCount;
    private BigDecimal harvestMoney;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

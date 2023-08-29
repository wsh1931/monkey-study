package com.monkey.monkeyUtils.pojo;

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
 * @date: 2023/8/28 20:36
 * @version: 1.0
 * @description: 退款信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundInformation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderInformationId;
    private String refundId;
    private Float totalMoney;
    private Float refund;
    private String reason;
    private String refundStatus;
    private String contentReturn;
    private String contentNotify;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;
}

package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/8/24 14:47
 * @version: 1.0
 * @description: 订单类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInformation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long associationId;
    private String title;
    private Float orderMoney;
    private String codeUrl;
    private String orderStatus;
    private String orderType;
    private String picture;
    private String payWay;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date submitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date payTime;
}

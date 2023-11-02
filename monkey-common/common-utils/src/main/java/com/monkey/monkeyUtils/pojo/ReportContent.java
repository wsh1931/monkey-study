package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/1 11:06
 * @version: 1.0
 * @description:
 */
@Data
public class ReportContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long reportTypeId;
    private Long associateId;
    private Integer type;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

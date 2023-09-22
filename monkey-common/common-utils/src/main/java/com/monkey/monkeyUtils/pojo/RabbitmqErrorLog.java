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
 * @description: 错误消息日志
 * @date: 2023/7/6 10:02
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitmqErrorLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String exchange;
    private String routingKey;
    private Integer tryCount;
    private String content;
    private String errorCause;
    private String errorEvent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

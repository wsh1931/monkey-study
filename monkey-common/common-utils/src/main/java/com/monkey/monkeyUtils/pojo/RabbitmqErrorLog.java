package com.monkey.monkeyUtils.pojo;

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
    private String correlationDataId;
    private String exchange;
    private String routingKey;
    private Integer tryCount;
    private String content;
    private String errorCause;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

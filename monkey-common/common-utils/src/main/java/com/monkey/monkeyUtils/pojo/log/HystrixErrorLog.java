package com.monkey.monkeyUtils.pojo.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @description: 发生远程服务器调用错误日志
 * @date: 2023/7/13 10:15
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HystrixErrorLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String methodName;
    private String serviceName;
    private String params;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

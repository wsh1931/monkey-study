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
 * @date: 2023/7/20 21:43
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long sort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

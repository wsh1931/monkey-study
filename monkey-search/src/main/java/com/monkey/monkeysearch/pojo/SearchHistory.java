package com.monkey.monkeysearch.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/17 20:18
 * @version: 1.0
 * @description:
 */
@Data
public class SearchHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

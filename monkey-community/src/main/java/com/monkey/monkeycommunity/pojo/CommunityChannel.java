package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/2 17:19
 * @version: 1.0
 * @description:
 */
@Data
public class CommunityChannel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long sort;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

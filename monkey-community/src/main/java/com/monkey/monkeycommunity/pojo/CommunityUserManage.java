package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/1 14:38
 * @version: 1.0
 * @description:
 */
@Data
public class CommunityUserManage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long communityId;
    private Long userId;
    private Integer isPrime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String headImg;
}

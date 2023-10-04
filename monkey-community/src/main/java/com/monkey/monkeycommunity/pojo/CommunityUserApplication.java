package com.monkey.monkeycommunity.pojo;

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
 * @date: 2023/10/1 9:24
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityUserApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long communityId;
    private Long userId;
    private Integer status;
    private Long approvalId;
    /**
     * 创造时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private String headImg;
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String approvalHeadImg;
    @TableField(exist = false)
    private String approvalUsername;
    @TableField(exist = false)
    private String brief;
}

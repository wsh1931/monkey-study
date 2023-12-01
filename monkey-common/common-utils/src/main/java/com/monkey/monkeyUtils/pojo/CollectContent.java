package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/7/31 8:29
 * @version: 1.0
 * @description: 收藏目录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private Long collectCount;
    private Integer isPrivate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private Integer isCollect;

    @TableField(exist = false)
    private Integer isShow = CommonEnum.COLLECT_NOT_SHOW.getCode();
}

package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/18 22:10
 * @version: 1.0
 * @description:
 */
@Data
public class UserOpusStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer articleCount;
    private Integer questionCount;
    private Integer courseCount;
    private Integer communityArticleCount;
    private Integer resourceCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

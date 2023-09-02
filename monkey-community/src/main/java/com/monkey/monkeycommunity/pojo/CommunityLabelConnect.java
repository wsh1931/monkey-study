package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/2 17:03
 * @version: 1.0
 * @description:
 */
@Data
public class CommunityLabelConnect {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long communityId;
    private Long communityClassificationLabelId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

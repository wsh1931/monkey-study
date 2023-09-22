package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/19 15:39
 * @version: 1.0
 * @description:
 */
@Data
public class CommunityArticleVoteUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long communityArticleVoteItemId;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}

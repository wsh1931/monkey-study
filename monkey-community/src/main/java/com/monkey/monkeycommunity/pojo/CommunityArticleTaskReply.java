package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/14 14:24
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityArticleTaskReply {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long communityArticleTaskId;
    private String replyContent;
    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date udpateTime;
}

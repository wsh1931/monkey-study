package com.monkey.monkeycommunity.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Long id;
    @ExcelIgnore
    private Long userId;
    @ExcelIgnore
    private Long communityArticleTaskId;
    @ExcelProperty(value = "回复内容", index = 1)
    private String replyContent;
    /**
     * 回复时间
     */
    @ExcelProperty(value = "回复时间", index = 3)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    @ExcelProperty(value = "用户名", index = 0)
    @TableField(exist = false)
    private String username;

    @ExcelIgnore
    @TableField(exist = false)
    private String headImg;

    @TableField(exist = false)
    @ExcelProperty(value = "提交次数", index = 2)
    private Integer submitCount;
}

package com.monkey.monkeyquestion.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private Long viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer replyCount;
    private String profile;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private List<String> labelName;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String userHeadImg;
    @TableField(exist = false)
    private String userBrief;
    @TableField(exist = false)
    private String photo;
}

package com.monkey.monkeyarticle.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.constant.ArticleEnum;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private String profile;
    private String photo;
    private Long sort;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    private Long updateUser;
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
    private Integer isHover = ArticleEnum.NOT_HOVER.getCode();
    @TableField(exist = false)
    private Integer isMoreHover = ArticleEnum.NOT_HOVER.getCode();
    @TableField(exist = false)
    private List<Label> labelList = new ArrayList<>();
}
